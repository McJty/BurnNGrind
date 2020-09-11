package mcjty.burnngrind.datagen;

import mcjty.burnngrind.BurnNGrind;
import mcjty.burnngrind.modules.furnaceplus.FurnacePlusModule;
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
        BlockModelBuilder main = models().cube("furnaceblock",
                modLoc("block/furnaceplus_side"),
                modLoc("block/furnaceplus_top"),
                modLoc("block/furnaceplus_front"),
                modLoc("block/furnaceplus_side"),
                modLoc("block/furnaceplus_side"),
                modLoc("block/furnaceplus_side"))
                .texture("particle", modLoc("block/furnaceplus_front"));

        BlockModelBuilder overlayNone = models().getBuilder("block/furnaceplus_front_on")
                .element().from(0, 0, 0).to(16, 16, 16).face(Direction.NORTH).cullface(Direction.NORTH).texture("#overlay").end().end()
                .texture("overlay", modLoc("block/furnaceplus_front_on"));

        getMultipartBuilder(FurnacePlusModule.FURNACEPLUS.get())
                .part().modelFile(main).addModel().condition(BlockStateProperties.FACING, Direction.NORTH).end()
                .part().modelFile(main).rotationY(180).addModel().condition(BlockStateProperties.FACING, Direction.SOUTH).end()
                .part().modelFile(main).rotationY(270).addModel().condition(BlockStateProperties.FACING, Direction.WEST).end()
                .part().modelFile(main).rotationY(90).addModel().condition(BlockStateProperties.FACING, Direction.EAST).end()
                .part().modelFile(main).rotationX(-90).addModel().condition(BlockStateProperties.FACING, Direction.UP).end()
                .part().modelFile(main).rotationX(90).addModel().condition(BlockStateProperties.FACING, Direction.DOWN).end()
                .part().modelFile(overlayNone).addModel().condition(BlockStateProperties.LIT, true).end();
    }
}
