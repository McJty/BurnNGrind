package mcjty.burnngrind.datagen;

import mcjty.burnngrind.BurnNGrind;
import mcjty.burnngrind.modules.furnaceplus.FurnacePlusModule;
import mcjty.burnngrind.modules.furnaceplus.blocks.FurnacePlusTileEntity;
import mcjty.lib.datagen.BaseBlockStateProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ExistingFileHelper;

public class BlockStates extends BaseBlockStateProvider {

    public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, BurnNGrind.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        registerFurnacePlusModel();
    }

    private void registerFurnacePlusModel() {
        for (int i = 0 ; i < FurnacePlusTileEntity.MAX_BURNS ; i++) {
            BlockModelBuilder main = models().cube("furnaceplus" + (i+1),
                    modLoc("block/furnaceplus_top"),
                    modLoc("block/furnaceplus_top"),
                    modLoc("block/furnaceplus_front"),
                    modLoc("block/furnaceplus_side"),
                    modLoc("block/furnaceplus_side"),
                    modLoc("block/furnaceplus_side"))
                    .texture("particle", modLoc("block/furnaceplus_front"));

            BlockModelBuilder overlayNone = models().getBuilder("block/furnaceplus_front_on" + (i+1))
                    .element().from(0, 0, 0).to(16, 16, 16).face(Direction.NORTH).cullface(Direction.NORTH).texture("#overlay").end().end()
                    .texture("overlay", modLoc("block/furnaceplus_front_on"));

            getMultipartBuilder(FurnacePlusModule.FURNACEPLUS[i].get())
                    .part().modelFile(main).addModel().condition(BlockStateProperties.FACING, Direction.NORTH).end()
                    .part().modelFile(main).rotationY(180).addModel().condition(BlockStateProperties.FACING, Direction.SOUTH).end()
                    .part().modelFile(main).rotationY(270).addModel().condition(BlockStateProperties.FACING, Direction.WEST).end()
                    .part().modelFile(main).rotationY(90).addModel().condition(BlockStateProperties.FACING, Direction.EAST).end()
                    .part().modelFile(main).rotationX(-90).addModel().condition(BlockStateProperties.FACING, Direction.UP).end()
                    .part().modelFile(main).rotationX(90).addModel().condition(BlockStateProperties.FACING, Direction.DOWN).end()
                    .part().modelFile(overlayNone).addModel().condition(BlockStateProperties.LIT, true).condition(BlockStateProperties.FACING, Direction.NORTH).end()
                    .part().modelFile(overlayNone).rotationY(180).addModel().condition(BlockStateProperties.LIT, true).condition(BlockStateProperties.FACING, Direction.SOUTH).end()
                    .part().modelFile(overlayNone).rotationY(270).addModel().condition(BlockStateProperties.LIT, true).condition(BlockStateProperties.FACING, Direction.WEST).end()
                    .part().modelFile(overlayNone).rotationY(90).addModel().condition(BlockStateProperties.LIT, true).condition(BlockStateProperties.FACING, Direction.EAST).end()
                    .part().modelFile(overlayNone).rotationX(-90).addModel().condition(BlockStateProperties.LIT, true).condition(BlockStateProperties.FACING, Direction.UP).end()
                    .part().modelFile(overlayNone).rotationX(90).addModel().condition(BlockStateProperties.LIT, true).condition(BlockStateProperties.FACING, Direction.DOWN).end();
        }
    }
}
