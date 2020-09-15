package mcjty.burnngrind.modules.furnaceplus.client;

import mcjty.burnngrind.BurnNGrind;
import mcjty.burnngrind.modules.furnaceplus.FurnacePlusModule;
import mcjty.burnngrind.modules.furnaceplus.blocks.FurnacePlusTileEntity;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.ManualEntry;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.Panel;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class GuiFurnacePlus extends GenericGuiContainer<FurnacePlusTileEntity, GenericContainer> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(BurnNGrind.MODID, "textures/gui/furnaceplus.png");

    public static final int WIDTH = 180;
    public static final int HEIGHT = 172;

    public GuiFurnacePlus(FurnacePlusTileEntity tileEntity, GenericContainer container, PlayerInventory inventory) {
        super(tileEntity, container, inventory, ManualEntry.EMPTY);
    }

    public static void register() {
        register(FurnacePlusModule.CONTAINER_FURNACEPLUS.get(), GuiFurnacePlus::new);
    }

    @Override
    public void init() {
        super.init();
        Panel toplevel = new Panel()
                .background(BACKGROUND);
        toplevel.bounds(guiLeft, guiTop, xSize, ySize);
        window = new Window(this, toplevel);
    }
}
