package mcjty.burnngrind.modules.furnaceplus.client;

import mcjty.burnngrind.BurnNGrind;
import mcjty.burnngrind.modules.furnaceplus.FurnacePlusModule;
import mcjty.burnngrind.modules.furnaceplus.blocks.FurnacePlusTileEntity;
import mcjty.burnngrind.setup.BurnNGrindMessages;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.EnergyBar;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

public class GuiFurnacePlus extends GenericGuiContainer<FurnacePlusTileEntity, GenericContainer> {

    private EnergyBar energyBar;

    public GuiFurnacePlus(FurnacePlusTileEntity tileEntity, GenericContainer container, PlayerInventory inventory) {
        super(tileEntity, container, inventory, ManualEntry.EMPTY);
    }

    public static void register() {
        register(FurnacePlusModule.CONTAINER_FURNACEPLUS.get(), GuiFurnacePlus::new);
    }

    @Override
    public void init() {
        window = new Window(this, tileEntity, BurnNGrindMessages.INSTANCE, new ResourceLocation(BurnNGrind.MODID, "gui/furnaceplus.gui"));
        super.init();
    }
}
