package dev.chililisoup.condiments.neoforge;

import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.neoforge.compat.create.CreateCompat;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(Condiments.MOD_ID)
public class CondimentsNeoForge {
    public CondimentsNeoForge(IEventBus modEventBus) {
        Condiments.init();

        if (FMLEnvironment.dist == Dist.CLIENT)
            CondimentsClientNeoForge.init();

        if (PlatHelper.isModLoaded("create"))
            CreateCompat.init(modEventBus);
    }
}