package mcjty.burnngrind.datagen;

import mcjty.burnngrind.modules.furnaceplus.FurnacePlusModule;
import mcjty.burnngrind.modules.furnaceplus.blocks.FurnacePlusTileEntity;
import mcjty.lib.datagen.BaseLootTableProvider;
import net.minecraft.data.DataGenerator;

public class LootTables extends BaseLootTableProvider {

    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        for (int i = 0 ; i < FurnacePlusTileEntity.MAX_BURNS ; i++) {
            lootTables.put(FurnacePlusModule.FURNACEPLUS[i].get(), createStandardTable("furnaceplus" + (i+1), FurnacePlusModule.FURNACEPLUS[i].get()));
        }
}
}
