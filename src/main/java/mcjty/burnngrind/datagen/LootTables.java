package mcjty.burnngrind.datagen;

import mcjty.burnngrind.modules.furnaceplus.FurnacePlusModule;
import mcjty.lib.datagen.BaseLootTableProvider;
import net.minecraft.data.DataGenerator;

public class LootTables extends BaseLootTableProvider {

    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        lootTables.put(FurnacePlusModule.FURNACEPLUS.get(), createStandardTable("furnaceplus", FurnacePlusModule.FURNACEPLUS.get()));
}
}
