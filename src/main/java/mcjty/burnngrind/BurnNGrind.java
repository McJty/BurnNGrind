package mcjty.burnngrind;

import mcjty.burnngrind.setup.ClientSetup;
import mcjty.burnngrind.setup.ModSetup;
import mcjty.burnngrind.setup.Registration;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


@Mod(BurnNGrind.MODID)
public class BurnNGrind {

    public static final String MODID = "burnngrind";

    @SuppressWarnings("PublicField")
    public static ModSetup setup = new ModSetup();

    public BurnNGrind() {
        Registration.register();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(setup::init);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::init);
        });
    }
}
