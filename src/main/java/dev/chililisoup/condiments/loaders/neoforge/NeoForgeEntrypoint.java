//? if neoforge {
/*package dev.chililisoup.condiments.loaders.neoforge;

import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.compat.create.CreateCompat;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Condiments.MOD_ID)
public class NeoForgeEntrypoint {
    public NeoForgeEntrypoint(IEventBus modEventBus) {
        Condiments.init();

        if (PlatHelper.isModLoaded("create"))
            CreateCompat.init(modEventBus);
    }
}
*///?}