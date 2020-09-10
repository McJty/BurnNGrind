package mcjty.burnngrind.setup;


import mcjty.burnngrind.BurnNGrind;
import mcjty.burnngrind.blocks.FurnacePlusTileEntity;
import net.minecraft.block.Block;
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

    public static void register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<Block> FURNACEPLUS = BLOCKS.register("furnaceplus", FurnacePlusTileEntity::createBlock);
    public static final RegistryObject<Item> FURNACEPLUS_ITEM = ITEMS.register("furnaceplus", () -> new BlockItem(FURNACEPLUS.get(), createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_FURNACEPLUS = TILES.register("furnaceplus", () -> TileEntityType.Builder.create(FurnacePlusTileEntity::new, FURNACEPLUS.get()).build(null));


    public static Item.Properties createStandardProperties() {
        return new Item.Properties().group(BurnNGrind.setup.getTab());
    }
}