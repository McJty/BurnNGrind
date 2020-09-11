package mcjty.burnngrind.setup;

import mcjty.burnngrind.modules.furnaceplus.FurnacePlusModule;
import mcjty.lib.setup.DefaultModSetup;
import net.minecraft.item.ItemStack;

public class ModSetup extends DefaultModSetup {

    public ModSetup() {
        createTab("restrictions", () -> new ItemStack(FurnacePlusModule.FURNACEPLUS.get()));
        BurnNGrindMessages.registerMessages("burnngrind");
    }

    @Override
    protected void setupModCompat() { }
}
