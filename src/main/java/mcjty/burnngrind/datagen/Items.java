package mcjty.burnngrind.datagen;

import mcjty.burnngrind.BurnNGrind;
import mcjty.burnngrind.modules.furnaceplus.FurnacePlusModule;
import mcjty.burnngrind.modules.furnaceplus.blocks.FurnacePlusTileEntity;
import mcjty.burnngrind.setup.Registration;
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

        itemGenerated(Registration.BASIC_SPEED_UPGRADE.get(), "item/basic_speed_upgrade");
        itemGenerated(Registration.SUPER_SPEED_UPGRADE.get(), "item/super_speed_upgrade");

    }

    @Override
    public String getName() {
        return "BurnNGrind Item Models";
    }
}
