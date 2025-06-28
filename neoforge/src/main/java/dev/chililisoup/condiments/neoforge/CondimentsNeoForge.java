package dev.chililisoup.condiments.neoforge;

import dev.chililisoup.condiments.Condiments;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Condiments.MOD_ID)
public class CondimentsNeoForge {
    public CondimentsNeoForge() {
        Condiments.init();

        if (FMLEnvironment.dist == Dist.CLIENT)
            CondimentsClientNeoForge.init();

        NeoForge.EVENT_BUS.register(new ModNeoForgeEventHandlers());
    }
}