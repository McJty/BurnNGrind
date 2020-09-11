package mcjty.burnngrind;

import mcjty.burnngrind.modules.furnaceplus.FurnacePlusModule;
import mcjty.burnngrind.setup.ModSetup;
import mcjty.burnngrind.setup.Registration;
import mcjty.lib.modules.Modules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


@Mod(BurnNGrind.MODID)
public class BurnNGrind {

    public static final String MODID = "burnngrind";

    @SuppressWarnings("PublicField")
    public static ModSetup setup = new ModSetup();
    private Modules modules = new Modules();

    public BurnNGrind() {
        setupModules();
        Registration.register();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(setup::init);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(modules::init);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(modules::initClient);
        });
    }

    private void setupModules() {
        modules.register(new FurnacePlusModule());

    }
}
