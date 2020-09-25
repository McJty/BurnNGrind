package mcjty.burnngrind.modules.furnaceplus.blocks;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import mcjty.burnngrind.BurnNGrind;
import mcjty.burnngrind.items.IUpgrade;
import mcjty.burnngrind.modules.furnaceplus.FurnacePlusModule;
import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.api.container.IContainerDataListener;
import mcjty.lib.container.AutomationFilterItemHander;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.NoDirectionItemHander;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Blocks;
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
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static mcjty.lib.container.ContainerFactory.CONTAINER_CONTAINER;
import static mcjty.lib.container.SlotDefinition.*;

public class FurnacePlusTileEntity extends GenericTileEntity implements ITickableTileEntity {

    public static final int MAX_BURNS = 4;

    public static final int SLOT_FUEL = 0;
    public static final int SLOT_UPGRADE = 1;
    public static final int SLOT_INPUT = 2;
    public static final int SLOT_OUTPUT = SLOT_INPUT + MAX_BURNS;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(2+MAX_BURNS+MAX_BURNS)
            .slot(specific(AbstractFurnaceTileEntity::isFuel).in(), CONTAINER_CONTAINER, SLOT_FUEL, 28, 37)
            .slot(specific(FurnacePlusTileEntity::isUpgrade), CONTAINER_CONTAINER, SLOT_UPGRADE, 28, 67)
            .box(generic().in(), CONTAINER_CONTAINER, SLOT_INPUT, 76, 7, 1, 0, MAX_BURNS, 20)
            .box(craftResult().onCraft(FurnacePlusTileEntity::onCraft), CONTAINER_CONTAINER, SLOT_OUTPUT, 123, 7, 1, 0, MAX_BURNS, 20)
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

    private final int maxBurns;

    private int burnTime = 0;
    private int burnTimeTotal = 0;
    private int cookTime[] = new int[MAX_BURNS];
    private int cookTimeTotal[] = new int[MAX_BURNS];
    private final Map<ResourceLocation, Integer> recipes = Maps.newHashMap();

    public FurnacePlusTileEntity(int maxBurns) {
        super(FurnacePlusModule.TYPE_FURNACEPLUS[maxBurns-1].get());
        this.maxBurns = maxBurns;
    }

    public NoDirectionItemHander getItems() {
        return items;
    }

    private static boolean isUpgrade(ItemStack stack) {
        return stack.getItem() instanceof IUpgrade;
    }

    public int getMaxBurns() {
        return maxBurns;
    }

    public boolean isBurning() {
        return burnTime > 0;
    }

    public int getCookProgressionScaled(int index) {
        int time = this.cookTime[index];
        int total = this.cookTimeTotal[index];
        return total != 0 && time != 0 ? time * 24 / total : 0;
    }

    private int getBurnTime(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        } else {
            Item item = fuel.getItem();
            return net.minecraftforge.common.ForgeHooks.getBurnTime(fuel);
        }
    }

    public int getSpeedupFactor() {
        ItemStack stack = items.getStackInSlot(SLOT_UPGRADE);
        if (stack.getItem() instanceof IUpgrade) {
            return ((IUpgrade) stack.getItem()).getFactor();
        } else {
            return 100;
        }
    }

    private int getCookTime(int index) {
        CRAFTING_INVENTORY.setInventorySlotContents(0, items.getStackInSlot(SLOT_INPUT + index));
        int cookTime = world.getRecipeManager().getRecipe(IRecipeType.SMELTING, CRAFTING_INVENTORY, this.world).map(AbstractCookingRecipe::getCookTime).orElse(200);
        return cookTime * 100 / getSpeedupFactor();
    }

    public int getBurnLeftScaled() {
        int total = this.burnTimeTotal;
        if (total == 0) {
            total = 200;
        }
        return this.burnTime * 13 / total;
    }

    private boolean canSmelt(int index, @Nullable IRecipe<?> recipeIn) {
        if (!items.getStackInSlot(SLOT_FUEL).isEmpty() && recipeIn != null) {
            ItemStack recipeOutput = recipeIn.getRecipeOutput();
            if (recipeOutput.isEmpty()) {
                return false;
            } else {
                ItemStack outputStack = items.getStackInSlot(SLOT_OUTPUT+index);
                if (outputStack.isEmpty()) {
                    return true;
                } else if (!outputStack.isItemEqual(recipeOutput)) {
                    return false;
                } else if (outputStack.getCount() + recipeOutput.getCount() <= 64 && outputStack.getCount() + recipeOutput.getCount() <= outputStack.getMaxStackSize()) {
                    return true;
                } else {
                    return outputStack.getCount() + recipeOutput.getCount() <= recipeOutput.getMaxStackSize();
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
        if (burning) {
            --this.burnTime;
            dirty = true;
        }

        if (!this.world.isRemote) {
            dirty = true;
            ItemStack fuelStack = items.getStackInSlot(SLOT_FUEL);
            for (int index = 0 ; index < maxBurns ; index++) {
                if ((this.isBurning() || !fuelStack.isEmpty()) && !items.getStackInSlot(SLOT_INPUT + index).isEmpty()) {
                    CRAFTING_INVENTORY.setInventorySlotContents(0, items.getStackInSlot(SLOT_INPUT + index));
                    IRecipe<?> recipe = this.world.getRecipeManager().getRecipe(IRecipeType.SMELTING, CRAFTING_INVENTORY, this.world).orElse(null);
                    if (!this.isBurning() && this.canSmelt(index, recipe)) {
                        startBurning(fuelStack);
                    }
                    handleBurning(index, recipe);
                } else if (!this.isBurning() && this.cookTime[index] > 0) {
                    this.cookTime[index] = MathHelper.clamp(this.cookTime[index] - 2, 0, this.cookTimeTotal[index]);
                }
            }

            if (burning != this.isBurning()) {
                this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(AbstractFurnaceBlock.LIT, Boolean.valueOf(this.isBurning())),
                        Constants.BlockFlags.BLOCK_UPDATE + Constants.BlockFlags.NOTIFY_NEIGHBORS);
            }
        }

        if (dirty) {
            this.markDirtyQuick();
        }

    }

    private void handleBurning(int index, IRecipe<?> recipe) {
        if (this.isBurning() && this.canSmelt(index, recipe)) {
            ++this.cookTime[index];
            if (this.cookTime[index] >= this.cookTimeTotal[index]) {
                this.cookTime[index] = 0;
                this.cookTimeTotal[index] = this.getCookTime(index);
                this.smelt(index, recipe);
            }
        } else {
            this.cookTime[index] = 0;
        }
    }

    private void startBurning(ItemStack fuelStack) {
        this.burnTime = this.getBurnTime(fuelStack);
        this.burnTimeTotal = this.burnTime;
        if (this.isBurning()) {
            if (fuelStack.hasContainerItem()) {
                items.setStackInSlot(SLOT_FUEL, fuelStack.getContainerItem());
            } else if (!fuelStack.isEmpty()) {
                fuelStack.shrink(1);
                if (fuelStack.isEmpty()) {
                    items.setStackInSlot(SLOT_FUEL, fuelStack.getContainerItem());
                }
            }
        }
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        this.burnTime = compound.getInt("BurnTime");
        if (compound.contains("CookTime")) {
            int[] cookTimes = compound.getIntArray("CookTime");
            System.arraycopy(cookTimes, 0, this.cookTime, 0, Math.min(cookTimes.length, this.cookTime.length));
        }
        if (compound.contains("CookTimeTotal")) {
            int[] cookTimeTotals = compound.getIntArray("CookTimeTotal");
            System.arraycopy(cookTimeTotals, 0, this.cookTimeTotal, 0, Math.min(cookTimeTotals.length, this.cookTimeTotal.length));
        }
        int recUsed = compound.getShort("RecipesUsedSize");

        for(int j = 0; j < recUsed; ++j) {
            ResourceLocation resourcelocation = new ResourceLocation(compound.getString("RecipeLocation" + j));
            this.recipes.put(resourcelocation, compound.getInt("RecipeAmount" + j));
        }
    }

    @Override
    protected void readCaps(CompoundNBT tagCompound) {
        super.readCaps(tagCompound);
        // Has to be done here so we have our items
        this.burnTimeTotal = this.getBurnTime(this.items.getStackInSlot(SLOT_FUEL));
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        compound.putInt("BurnTime", this.burnTime);
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


    private void smelt(int index, @Nullable IRecipe<?> recipe) {
        if (recipe != null && this.canSmelt(index, recipe)) {
            ItemStack inputStack = items.getStackInSlot(SLOT_INPUT+index);
            ItemStack recipeOutput = recipe.getRecipeOutput();
            ItemStack outputStack = items.getStackInSlot(SLOT_OUTPUT+index);
            if (outputStack.isEmpty()) {
                this.items.setStackInSlot(SLOT_OUTPUT+index, recipeOutput.copy());
            } else if (outputStack.getItem() == recipeOutput.getItem()) {
                outputStack.grow(recipeOutput.getCount());
            }

            if (!this.world.isRemote) {
                this.setRecipeUsed(recipe);
            }

            if (inputStack.getItem() == Blocks.WET_SPONGE.asItem() && !items.getStackInSlot(SLOT_INPUT+index).isEmpty() && items.getStackInSlot(SLOT_INPUT+index).getItem() == Items.BUCKET) {
                items.setStackInSlot(SLOT_INPUT+index, new ItemStack(Items.WATER_BUCKET));
            }

            inputStack.shrink(1);
        }
    }


    private NoDirectionItemHander createItemHandler() {
        return new NoDirectionItemHander(this, CONTAINER_FACTORY.get()) {

            private boolean isDifferentInput(int index, ItemStack originalStack, ItemStack inputStack) {
                if (index >= SLOT_INPUT && index < SLOT_INPUT + MAX_BURNS) {
                    if (inputStack.isEmpty() || !inputStack.isItemEqual(originalStack) || !ItemStack.areItemStackTagsEqual(inputStack, originalStack)) {
                        return true;
                    }
                }
                return false;
            }

            private void updateCookTime(int index) {
                FurnacePlusTileEntity.this.cookTimeTotal[index] = FurnacePlusTileEntity.this.getCookTime(index);
                FurnacePlusTileEntity.this.cookTime[index] = 0;
            }

            @Override
            public void setInventorySlotContents(int stackLimit, int index, ItemStack stack) {
                boolean differentInput = isDifferentInput(index, getStackInSlot(index), stack);
                super.setInventorySlotContents(stackLimit, index, stack);
                if (differentInput) {
                    updateCookTime(index - SLOT_INPUT);
                }
            }

            @Override
            public ItemStack decrStackSize(int index, int amount) {
                ItemStack rc = super.decrStackSize(index, amount);
                if (index >= SLOT_INPUT && index < SLOT_INPUT + MAX_BURNS) {
                    updateCookTime(index - SLOT_INPUT);
                }
                return rc;
            }

            @Override
            public void setStackInSlot(int index, @Nonnull ItemStack stack) {
                boolean differentInput = isDifferentInput(index, getStackInSlot(index), stack);
                super.setStackInSlot(index, stack);
                if (differentInput) {
                    updateCookTime(index - SLOT_INPUT);
                }
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (slot == SLOT_FUEL) {
                    return AbstractFurnaceTileEntity.isFuel(stack);
                } else if (slot == SLOT_UPGRADE) {
                    return isUpgrade(stack);
                } else {
                    int burnIdx;
                    if (slot >= SLOT_INPUT && slot < SLOT_OUTPUT) {
                        burnIdx = slot - SLOT_INPUT;
                    } else {
                        burnIdx = slot - SLOT_OUTPUT;
                    }
                    return burnIdx < maxBurns;
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

        private int prevBurnTime = -1;
        private int prevBurnTimeTotal = -1;
        private int prevCookTime[] = new int[] { -1, -1, -1, -1 };
        private int prevCookTimeTotal[] = new int[] { -1, -1, -1, -1 };

        public DataListener(FurnacePlusTileEntity tileEntity) {
            this.tileEntity = tileEntity;
        }

        @Override
        public ResourceLocation getId() {
            return ID;
        }

        @Override
        public boolean isDirtyAndClear() {
            boolean dirty = false;
            if (prevBurnTime != tileEntity.burnTime) {
                prevBurnTime = tileEntity.burnTime;
                dirty = true;
            }
            if (prevBurnTimeTotal != tileEntity.burnTimeTotal) {
                prevBurnTimeTotal = tileEntity.burnTimeTotal;
                dirty = true;
            }
            for (int index = 0 ; index < MAX_BURNS ; index++) {
                if (prevCookTime[index] != tileEntity.cookTime[index]) {
                    prevCookTime[index] = tileEntity.cookTime[index];
                    dirty = true;
                }
                if (prevCookTimeTotal[index] != tileEntity.cookTimeTotal[index]) {
                    prevCookTimeTotal[index] = tileEntity.cookTimeTotal[index];
                    dirty = true;
                }
            }
            return dirty;
        }

        @Override
        public void toBytes(PacketBuffer buf) {
            buf.writeInt(tileEntity.burnTime);
            buf.writeInt(tileEntity.burnTimeTotal);
            for (int index = 0 ; index < MAX_BURNS ; index++) {
                buf.writeInt(tileEntity.cookTime[index]);
                buf.writeInt(tileEntity.cookTimeTotal[index]);
            }
        }

        @Override
        public void readBuf(PacketBuffer buf) {
            tileEntity.burnTime = buf.readInt();
            tileEntity.burnTimeTotal = buf.readInt();
            for (int index = 0 ; index < MAX_BURNS ; index++) {
                tileEntity.cookTime[index] = buf.readInt();
                tileEntity.cookTimeTotal[index] = buf.readInt();
            }
        }
    }
}
