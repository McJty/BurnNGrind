package mcjty.burnngrind.datagen;

import mcjty.burnngrind.BurnNGrind;
import mcjty.burnngrind.modules.furnaceplus.FurnacePlusModule;
import mcjty.burnngrind.modules.furnaceplus.blocks.FurnacePlusTileEntity;
import mcjty.lib.datagen.BaseItemModelProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;

public class Items extends BaseItemModelProvider {

    public Items(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, BurnNGrind.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (int i = 0 ; i < FurnacePlusTileEntity.MAX_BURNS ; i++) {
            parentedBlock(FurnacePlusModule.FURNACEPLUS[i].get(), "block/furnaceplus" + (i+1));
        }
    }

    @Override
    public String getName() {
        return "BurnNGrind Item Models";
    }
}
