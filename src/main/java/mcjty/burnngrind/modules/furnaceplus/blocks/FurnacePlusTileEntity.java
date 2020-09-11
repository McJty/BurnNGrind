package mcjty.burnngrind.modules.furnaceplus.blocks;

import mcjty.burnngrind.modules.furnaceplus.FurnacePlusModule;
import mcjty.lib.api.container.CapabilityContainerProvider;
import mcjty.lib.api.container.DefaultContainerProvider;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.AutomationFilterItemHander;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.container.NoDirectionItemHander;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.EnergyTools;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;
import static mcjty.lib.container.ContainerFactory.CONTAINER_CONTAINER;
import static mcjty.lib.container.SlotDefinition.output;
import static mcjty.lib.container.SlotDefinition.specific;

public class FurnacePlusTileEntity extends GenericTileEntity implements ITickableTileEntity {

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_FUEL = 1;
    public static final int SLOT_OUTPUT = 2;

    public static final Lazy<ContainerFactory> CONTAINER_FACTORY = Lazy.of(() -> new ContainerFactory(2)
            .slot(specific(new ItemStack(Items.COAL), new ItemStack(Items.CHARCOAL), new ItemStack(Blocks.COAL_BLOCK)),
                    CONTAINER_CONTAINER, SLOT_INPUT, 82, 24)
            .slot(output(), CONTAINER_CONTAINER, SLOT_OUTPUT, 118, 24)
            .playerSlots(10, 70));

    private final NoDirectionItemHander items = createItemHandler();
    private final LazyOptional<NoDirectionItemHander> itemHandler = LazyOptional.of(() -> items);
    private final LazyOptional<AutomationFilterItemHander> automationItemHandler = LazyOptional.of(() -> new AutomationFilterItemHander(items) {
        @Override
        public boolean canAutomationExtract(int slot) {
            return slot == SLOT_OUTPUT;
        }

        @Override
        public boolean canAutomationInsert(int slot) {
            return slot != SLOT_OUTPUT;
        }
    });

    private final LazyOptional<INamedContainerProvider> screenHandler = LazyOptional.of(() -> new DefaultContainerProvider<GenericContainer>("Crafter")
            .containerSupplier((windowId,player) -> new GenericContainer(FurnacePlusModule.CONTAINER_FURNACEPLUS.get(), windowId, CONTAINER_FACTORY.get(), getPos(), FurnacePlusTileEntity.this))
            .itemHandler(itemHandler));


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

    @Override
    public void tick() {

    }

    private NoDirectionItemHander createItemHandler() {
        return new NoDirectionItemHander(this, CONTAINER_FACTORY.get()) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (slot == SLOT_INPUT) {
                    return (stack.getItem() == Items.COAL || stack.getItem() == Items.CHARCOAL || stack.getItem() == Items.COAL_BLOCK);
                } else {
                    return EnergyTools.isEnergyItem(stack);
                }
            }

            @Override
            public boolean isItemInsertable(int slot, @Nonnull ItemStack stack) {
                return isItemValid(slot, stack);
            }

        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction facing) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return automationItemHandler.cast();
        }
        if (cap == CapabilityContainerProvider.CONTAINER_PROVIDER_CAPABILITY) {
            return screenHandler.cast();
        }
        return super.getCapability(cap, facing);
    }
}
