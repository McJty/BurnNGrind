package mcjty.burnngrind.modules.furnaceplus;

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

    public static final RegistryObject<Block> FURNACEPLUS = Registration.BLOCKS.register("furnaceplus", FurnacePlusTileEntity::createBlock);
    public static final RegistryObject<TileEntityType<?>> TYPE_FURNACEPLUS = Registration.TILES.register("furnaceplus", () -> TileEntityType.Builder.create(FurnacePlusTileEntity::new, FURNACEPLUS.get()).build(null));
    public static final RegistryObject<Item> FURNACEPLUS_ITEM = Registration.ITEMS.register("furnaceplus", () -> new BlockItem(FURNACEPLUS.get(), Registration.createStandardProperties()));
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
