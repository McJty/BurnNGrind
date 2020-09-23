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

public class GuiFurnacePlus extends GenericGuiContainer<FurnacePlusTileEntity, GenericContainer> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(BurnNGrind.MODID, "textures/gui/furnaceplus.png");

    public static final int WIDTH = 180;
    public static final int HEIGHT = 172;

    public GuiFurnacePlus(FurnacePlusTileEntity tileEntity, GenericContainer container, PlayerInventory inventory) {
        super(tileEntity, container, inventory, ManualEntry.EMPTY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        this.minecraft.getTextureManager().bindTexture(BACKGROUND);
        if (tileEntity.isBurning()) {
            int burnLeft = tileEntity.getBurnLeftScaled();
            this.blit(this.guiLeft + 28, this.guiTop + 20 + 12 - burnLeft, WIDTH, 12 - burnLeft, 14, burnLeft + 1);
        }

        for (int index = 0 ; index < FurnacePlusTileEntity.MAX_BURNS ; index++) {
            int cookProgression = tileEntity.getCookProgressionScaled(index);
            this.blit(this.guiLeft + 95, this.guiTop + 6 + index * 20, WIDTH, 14, cookProgression + 1, 16);
        }
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
