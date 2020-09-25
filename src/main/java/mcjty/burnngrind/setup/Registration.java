package mcjty.burnngrind.setup;


import mcjty.burnngrind.BurnNGrind;
import mcjty.burnngrind.items.BasicSpeedUpgrade;
import mcjty.burnngrind.items.SuperSpeedUpgrade;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static mcjty.burnngrind.BurnNGrind.MODID;

public class Registration {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MODID);
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);

    public static void register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<Item> BASIC_SPEED_UPGRADE = Registration.ITEMS.register("basic_speed_upgrade", BasicSpeedUpgrade::new);
    public static final RegistryObject<Item> SUPER_SPEED_UPGRADE = Registration.ITEMS.register("super_speed_upgrade", SuperSpeedUpgrade::new);

    public static Item.Properties createStandardProperties() {
        return new Item.Properties().group(BurnNGrind.setup.getTab());
    }
}