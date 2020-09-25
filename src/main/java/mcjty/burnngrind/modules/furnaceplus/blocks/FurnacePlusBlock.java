package mcjty.burnngrind.modules.furnaceplus.blocks;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.container.NoDirectionItemHander;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import static mcjty.lib.builder.TooltipBuilder.*;

public class FurnacePlusBlock extends BaseBlock {

    public FurnacePlusBlock(int maxBurns) {
        super(new BlockBuilder()
                .properties(Block.Properties.create(Material.ROCK)
                        .harvestTool(ToolType.PICKAXE)
                        .harvestLevel(0)
                        .hardnessAndResistance(2.0f)
                        .sound(SoundType.STONE))
                .tileEntitySupplier(() -> new FurnacePlusTileEntity(maxBurns))
                .info(key("message.burnngrind.shiftmessage"))
                .infoShift(header(),
                        parameter("speedup", stack -> getSpeedupFactor(stack) + "%")));
    }

    private static String getSpeedupFactor(ItemStack stack) {
//        return String.valueOf(NBTTools.getInfoNBT(stack, CompoundNBT::getInt, "speedup", 100));
        return "todo";
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(BlockStateProperties.LIT);
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof FurnacePlusTileEntity) {
                NoDirectionItemHander items = ((FurnacePlusTileEntity) tileentity).getItems();
                for (int i = 0 ; i < items.getSlots() ; i++) {
                    InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), items.getStackInSlot(i));
                }
                worldIn.updateComparatorOutputLevel(pos, this);
            }

            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

}
