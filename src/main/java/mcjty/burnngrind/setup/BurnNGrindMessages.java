package mcjty.burnngrind.setup;

import mcjty.burnngrind.BurnNGrind;
import mcjty.lib.network.PacketHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class BurnNGrindMessages {
    public static SimpleChannel INSTANCE;

    public static void registerMessages(String name) {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(BurnNGrind.MODID, name))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        PacketHandler.registerStandardMessages(id(), net);
    }

    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }
}
