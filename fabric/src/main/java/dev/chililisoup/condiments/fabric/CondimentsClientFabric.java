package dev.chililisoup.condiments.fabric;

import dev.chililisoup.condiments.client.renderer.CondimentsHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

@Environment(EnvType.CLIENT)
public class CondimentsClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(CondimentsHud::render);
    }
}
