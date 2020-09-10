package mcjty.burnngrind.datagen;

import mcjty.burnngrind.setup.Registration;
import mcjty.lib.datagen.BaseLootTableProvider;
import net.minecraft.data.DataGenerator;

public class LootTables extends BaseLootTableProvider {

    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        lootTables.put(Registration.FURNACEPLUS.get(), createStandardTable("furnaceplus", Registration.FURNACEPLUS.get()));
}
}
