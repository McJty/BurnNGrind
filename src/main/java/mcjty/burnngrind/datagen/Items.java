package mcjty.burnngrind.datagen;

import mcjty.burnngrind.BurnNGrind;
import mcjty.burnngrind.modules.furnaceplus.FurnacePlusModule;
import mcjty.lib.datagen.BaseItemModelProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;

public class Items extends BaseItemModelProvider {

    public Items(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, BurnNGrind.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        parentedBlock(FurnacePlusModule.FURNACEPLUS.get(),"block/furnaceplus");
    }

    @Override
    public String getName() {
        return "BurnNGrind Item Models";
    }
}
