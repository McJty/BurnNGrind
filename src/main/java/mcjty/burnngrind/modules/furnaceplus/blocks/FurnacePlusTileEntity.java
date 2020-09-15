package mcjty.burnngrind.modules.furnaceplus.blocks;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mcjty.burnngrind.BurnNGrind;
import mcjty.burnngrind.modules.furnaceplus.FurnacePlusModule;
import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.container.IContainerDataListener;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.AutomationFilterItemHander;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.NoDirectionItemHander;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;
import static mcjty.lib.container.ContainerFactory.CONTAINER_CONTAINER;
import static mcjty.lib.container.SlotDefinition.*;

public class FurnacePlusTileEntity extends GenericTileEntity implements ITickableTileEntity {

    public static final int SLOT_FUEL = 0;
    public static final int SLOT_INPUT = 1;
    public static final int SLOT_OUTPUT = 5;
    public static final int MAX_BURNS = 4;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(1+MAX_BURNS+MAX_BURNS)
            .slot(specific(AbstractFurnaceTileEntity::isFuel).in(), CONTAINER_CONTAINER, SLOT_FUEL, 27, 36)
            .box(generic().in(), CONTAINER_CONTAINER, SLOT_INPUT, 75, 6, 1, 0, MAX_BURNS, 20)
            .box(craftResult().onCraft(FurnacePlusTileEntity::onCraft), CONTAINER_CONTAINER, SLOT_OUTPUT, 122, 6, 1, 0, MAX_BURNS, 20)
            .playerSlots(10, 90));

    private final NoDirectionItemHander items = createItemHandler();
    private final LazyOptional<AutomationFilterItemHander> itemHandler = LazyOptional.of(() -> new AutomationFilterItemHander(items));

    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("FurnacePlus")
            .containerSupplier((windowId,player) -> new GenericContainer(FurnacePlusModule.CONTAINER_FURNACEPLUS.get(), windowId, CONTAINER_FACTORY.get(), getPos(), FurnacePlusTileEntity.this))
            .dataListener(new DataListener(this))
            .itemHandler(() -> items));

    private static final CraftingInventory CRAFTING_INVENTORY = new CraftingInventory(new Container(null, -1) {
        @Override
        public boolean canInteractWith(PlayerEntity playerIn) {
            return false;
        }
    }, 1, 1);

    private int burnTime[] = new int[MAX_BURNS];
    private int cookTime[] = new int[MAX_BURNS];
    private int cookTimeTotal[] = new int[MAX_BURNS];
    private final Map<ResourceLocation, Integer> recipes = Maps.newHashMap();

    public FurnacePlusTileEntity() {
        super(FurnacePlusModule.TYPE_FURNACEPLUS.get());
    }

    public static BaseBlock createBlock() {
        return new BaseBlock(new BlockBuilder()
                .properties(Block.Properties.create(Material.ROCK)
                        .harvestTool(ToolType.PICKAXE)
                        .harvestLevel(0)
                        .hardnessAndResistance(2.0f)
                        .sound(SoundType.STONE))
                .tileEntitySupplier(FurnacePlusTileEntity::new)
                .info(key("message.burnngrind.shiftmessage"))
                .infoShift(header())) {
            @Override
            protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
                super.fillStateContainer(builder);
                builder.add(BlockStateProperties.LIT);
            }
        };
    }

    private boolean isBurning() {
        return this.burnTime[0] > 0;
    }

    private int getBurnTime(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        } else {
            Item item = fuel.getItem();
            return net.minecraftforge.common.ForgeHooks.getBurnTime(fuel);
        }
    }

    private int getCookTime() {
        return this.world.getRecipeManager().getRecipe(IRecipeType.SMELTING, CRAFTING_INVENTORY, this.world).map(AbstractCookingRecipe::getCookTime).orElse(200);
    }


    private boolean canSmelt(@Nullable IRecipe<?> recipeIn) {
        if (!items.getStackInSlot(SLOT_FUEL).isEmpty() && recipeIn != null) {
            ItemStack itemstack = recipeIn.getRecipeOutput();
            if (itemstack.isEmpty()) {
                return false;
            } else {
                ItemStack itemstack1 = items.getStackInSlot(SLOT_OUTPUT+0);
                if (itemstack1.isEmpty()) {
                    return true;
                } else if (!itemstack1.isItemEqual(itemstack)) {
                    return false;
                } else if (itemstack1.getCount() + itemstack.getCount() <= 64 && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize()) {
                    return true;
                } else {
                    return itemstack1.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize();
                }
            }
        } else {
            return false;
        }
    }

    @Override
    public void tick() {
        boolean burning = this.isBurning();
        boolean dirty = false;
        if (this.isBurning()) {
            --this.burnTime[0];
        }

        if (!this.world.isRemote) {
            ItemStack itemstack = items.getStackInSlot(SLOT_FUEL);
            if (this.isBurning() || !itemstack.isEmpty() && !items.getStackInSlot(SLOT_INPUT+0).isEmpty()) {
                IRecipe<?> irecipe = this.world.getRecipeManager().getRecipe(IRecipeType.SMELTING, CRAFTING_INVENTORY, this.world).orElse(null);
                if (!this.isBurning() && this.canSmelt(irecipe)) {
                    this.burnTime[0] = this.getBurnTime(itemstack);
                    if (this.isBurning()) {
                        dirty = true;
                        if (itemstack.hasContainerItem())
                            items.setStackInSlot(SLOT_INPUT+0, itemstack.getContainerItem());
                        else
                        if (!itemstack.isEmpty()) {
                            Item item = itemstack.getItem();
                            itemstack.shrink(1);
                            if (itemstack.isEmpty()) {
                                items.setStackInSlot(SLOT_INPUT+0, itemstack.getContainerItem());
                            }
                        }
                    }
                }

                if (this.isBurning() && this.canSmelt(irecipe)) {
                    ++this.cookTime[0];
                    if (this.cookTime[0] == this.cookTimeTotal[0]) {
                        this.cookTime[0] = 0;
                        this.cookTimeTotal[0] = this.getCookTime();
                        this.smelt(irecipe);
                        dirty = true;
                    }
                } else {
                    this.cookTime[0] = 0;
                }
            } else if (!this.isBurning() && this.cookTime[0] > 0) {
                this.cookTime[0] = MathHelper.clamp(this.cookTime[0] - 2, 0, this.cookTimeTotal[0]);
            }

            if (burning != this.isBurning()) {
                dirty = true;
                this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(AbstractFurnaceBlock.LIT, Boolean.valueOf(this.isBurning())), 3);
            }
        }

        if (dirty) {
            this.markDirtyQuick();
        }

    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        if (compound.contains("BurnTime")) {
            this.burnTime = compound.getIntArray("BurnTime");
        }
        if (compound.contains("CookTime")) {
            this.cookTime = compound.getIntArray("CookTime");
        }
        if (compound.contains("CookTimeTotal")) {
            this.cookTimeTotal = compound.getIntArray("CookTimeTotal");
        }
        int i = compound.getShort("RecipesUsedSize");

        for(int j = 0; j < i; ++j) {
            ResourceLocation resourcelocation = new ResourceLocation(compound.getString("RecipeLocation" + j));
            int k = compound.getInt("RecipeAmount" + j);
            this.recipes.put(resourcelocation, k);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        compound.putIntArray("BurnTime", this.burnTime);
        compound.putIntArray("CookTime", this.cookTime);
        compound.putIntArray("CookTimeTotal", this.cookTimeTotal);
        compound.putShort("RecipesUsedSize", (short)this.recipes.size());
        int i = 0;

        for(Map.Entry<ResourceLocation, Integer> entry : this.recipes.entrySet()) {
            compound.putString("RecipeLocation" + i, entry.getKey().toString());
            compound.putInt("RecipeAmount" + i, entry.getValue());
            ++i;
        }

        return compound;
    }


    public void setRecipeUsed(@Nullable IRecipe<?> recipe) {
        if (recipe != null) {
            this.recipes.compute(recipe.getId(), (resloc, i) -> 1 + (i == null ? 0 : i));
        }
    }


    private void smelt(@Nullable IRecipe<?> recipe) {
        if (recipe != null && this.canSmelt(recipe)) {
            ItemStack fuelStack = items.getStackInSlot(SLOT_FUEL);
            ItemStack recipeOutput = recipe.getRecipeOutput();
            ItemStack outputStack = items.getStackInSlot(SLOT_OUTPUT+0);
            if (outputStack.isEmpty()) {
                this.items.setStackInSlot(SLOT_OUTPUT+0, recipeOutput.copy());
            } else if (outputStack.getItem() == recipeOutput.getItem()) {
                outputStack.grow(recipeOutput.getCount());
            }

            if (!this.world.isRemote) {
                this.setRecipeUsed(recipe);
            }

            if (fuelStack.getItem() == Blocks.WET_SPONGE.asItem() && !items.getStackInSlot(SLOT_INPUT+0).isEmpty() && items.getStackInSlot(SLOT_INPUT+0).getItem() == Items.BUCKET) {
                items.setStackInSlot(SLOT_INPUT+0, new ItemStack(Items.WATER_BUCKET));
            }

            fuelStack.shrink(1);
        }
    }


    private NoDirectionItemHander createItemHandler() {
        return new NoDirectionItemHander(this, CONTAINER_FACTORY.get()) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (slot == SLOT_FUEL) {
                    return AbstractFurnaceTileEntity.isFuel(stack);
                } else {
                    return true;
                }
            }

            @Override
            public boolean isItemInsertable(int slot, @Nonnull ItemStack stack) {
                return isItemValid(slot, stack);
            }

        };
    }

    private Optional<FurnaceRecipe> getRecipe(ItemStack stack) {
        CRAFTING_INVENTORY.setInventorySlotContents(0, stack);
        return world.getRecipeManager().getRecipe(IRecipeType.SMELTING, CRAFTING_INVENTORY, world);
    }

    private boolean hasBurningRecipe(ItemStack stack) {
        return getRecipe(stack).isPresent();
    }

    public static void onCraft(TileEntity tileEntity, PlayerEntity player, ItemStack stack) {
        if (!(tileEntity instanceof FurnacePlusTileEntity)) {
            return;
        }
        FurnacePlusTileEntity te = (FurnacePlusTileEntity) tileEntity;

        List<IRecipe<?>> list = Lists.newArrayList();

        for(Map.Entry<ResourceLocation, Integer> entry : te.recipes.entrySet()) {
            player.world.getRecipeManager().getRecipe(entry.getKey()).ifPresent((recipe) -> {
                list.add(recipe);
                te.spawnExpOrbs(player, entry.getValue(), ((AbstractCookingRecipe)recipe).getExperience());
            });
        }

        player.unlockRecipes(list);
        te.recipes.clear();
    }

    private static void spawnExpOrbs(PlayerEntity player, int amount, float experience) {
        if (experience == 0.0F) {
            amount = 0;
        } else if (experience < 1.0F) {
            int i = MathHelper.floor(amount * experience);
            if (i < MathHelper.ceil(amount * experience) && Math.random() < (amount * experience - i)) {
                ++i;
            }

            amount = i;
        }

        while(amount > 0) {
            int j = ExperienceOrbEntity.getXPSplit(amount);
            amount -= j;
            player.world.addEntity(new ExperienceOrbEntity(player.world, player.getPosX(), player.getPosY() + 0.5D, player.getPosZ() + 0.5D, j));
        }

    }
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction facing) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemHandler.cast();
        }
        if (cap == CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY) {
            return screenHandler.cast();
        }
        return super.getCapability(cap, facing);
    }

    private static class DataListener implements IContainerDataListener {
        private static final ResourceLocation ID = new ResourceLocation(BurnNGrind.MODID, "furnaceplus");
        private final FurnacePlusTileEntity tileEntity;

        public DataListener(FurnacePlusTileEntity tileEntity) {
            this.tileEntity = tileEntity;
        }

        @Override
        public ResourceLocation getId() {
            return ID;
        }

        @Override
        public boolean isDirtyAndClear() {
            return false;
        }

        @Override
        public void toBytes(PacketBuffer buf) {

        }

        @Override
        public void readBuf(PacketBuffer buf) {

        }
    }
}
