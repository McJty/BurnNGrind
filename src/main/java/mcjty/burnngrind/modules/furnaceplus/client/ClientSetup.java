package mcjty.burnngrind.modules.furnaceplus.client;

import mcjty.burnngrind.modules.furnaceplus.FurnacePlusModule;
import mcjty.burnngrind.modules.furnaceplus.blocks.FurnacePlusTileEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;

public class ClientSetup {
    public static void initClient() {
        for (int i = 0 ; i < FurnacePlusTileEntity.MAX_BURNS ; i++) {
            RenderTypeLookup.setRenderLayer(FurnacePlusModule.FURNACEPLUS[i].get(), RenderType.getCutout());
        }
    }
}
