package mcjty.burnngrind.modules.furnaceplus;

import mcjty.burnngrind.modules.furnaceplus.blocks.FurnacePlusBlock;
import mcjty.burnngrind.modules.furnaceplus.blocks.FurnacePlusTileEntity;
import mcjty.burnngrind.modules.furnaceplus.client.GuiFurnacePlus;
import mcjty.burnngrind.setup.Registration;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.modules.IModule;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class FurnacePlusModule implements IModule {

    public static final RegistryObject<Block>[] FURNACEPLUS = new RegistryObject[] {
            Registration.BLOCKS.register("furnaceplus1", () -> new FurnacePlusBlock(1)),
            Registration.BLOCKS.register("furnaceplus2", () -> new FurnacePlusBlock(2)),
            Registration.BLOCKS.register("furnaceplus3", () -> new FurnacePlusBlock(3)),
            Registration.BLOCKS.register("furnaceplus4", () -> new FurnacePlusBlock(4)),
    };
    public static final RegistryObject<TileEntityType<?>>[] TYPE_FURNACEPLUS = new RegistryObject[] {
            Registration.TILES.register("furnaceplus1", () -> TileEntityType.Builder.create(() -> new FurnacePlusTileEntity(1), FURNACEPLUS[0].get()).build(null)),
            Registration.TILES.register("furnaceplus2", () -> TileEntityType.Builder.create(() -> new FurnacePlusTileEntity(2), FURNACEPLUS[1].get()).build(null)),
            Registration.TILES.register("furnaceplus3", () -> TileEntityType.Builder.create(() -> new FurnacePlusTileEntity(3), FURNACEPLUS[2].get()).build(null)),
            Registration.TILES.register("furnaceplus4", () -> TileEntityType.Builder.create(() -> new FurnacePlusTileEntity(4), FURNACEPLUS[3].get()).build(null))
    };
    public static final RegistryObject<Item>[] FURNACEPLUS_ITEM = new RegistryObject[] {
            Registration.ITEMS.register("furnaceplus1", () -> new BlockItem(FURNACEPLUS[0].get(), Registration.createStandardProperties())),
            Registration.ITEMS.register("furnaceplus2", () -> new BlockItem(FURNACEPLUS[1].get(), Registration.createStandardProperties())),
            Registration.ITEMS.register("furnaceplus3", () -> new BlockItem(FURNACEPLUS[2].get(), Registration.createStandardProperties())),
            Registration.ITEMS.register("furnaceplus4", () -> new BlockItem(FURNACEPLUS[3].get(), Registration.createStandardProperties())),
    };
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_FURNACEPLUS = Registration.CONTAINERS.register("furnaceplus", GenericContainer::createContainerType);

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        DeferredWorkQueue.runLater(() -> {
            GuiFurnacePlus.register();
        });
    }

    @Override
    public void initConfig() {

    }
}
