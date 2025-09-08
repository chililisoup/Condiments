//? if forgeLike {
/*package dev.chililisoup.condiments.loaders.neoforge;

import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.compat.create.CreateCompat;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;

//? if neoforge {
/^import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
^///?} else {
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
//?}

@Mod(Condiments.MOD_ID)
public class NeoForgeEntrypoint {
    //? if forge
    /^private final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();^/

    public NeoForgeEntrypoint(
            //? if neoforge
            /^IEventBus modEventBus^/
    ) {
        Condiments.init();

        if (PlatHelper.isModLoaded("create"))
            CreateCompat.init(modEventBus);
    }
}
*///?}