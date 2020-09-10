package mcjty.burnngrind.blocks;

import mcjty.burnngrind.setup.Registration;
import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.builder.BlockBuilder;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraftforge.common.ToolType;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

public class FurnacePlusTileEntity extends GenericTileEntity implements ITickableTileEntity {

    public FurnacePlusTileEntity() {
        super(Registration.TYPE_FURNACEPLUS.get());
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
}
