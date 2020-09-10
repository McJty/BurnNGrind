package mcjty.burnngrind.setup;

import mcjty.lib.setup.DefaultModSetup;
import net.minecraft.item.ItemStack;

public class ModSetup extends DefaultModSetup {

    public ModSetup() {
        createTab("restrictions", () -> new ItemStack(Registration.FURNACEPLUS.get()));
    }

    @Override
    protected void setupModCompat() { }
}
