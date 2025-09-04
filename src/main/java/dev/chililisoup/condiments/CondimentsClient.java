package dev.chililisoup.condiments;

import dev.chililisoup.condiments.client.renderer.CondimentsHud;
import dev.chililisoup.condiments.config.ClientConfig;
import dev.chililisoup.condiments.dynamicpack.ClientDynamicResourcesGenerator;
import dev.chililisoup.condiments.reg.ClientRegistry;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;

//? if fabric {
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
//?} elif neoforge {
/*import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;
*///?}

//$ client_only
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class CondimentsClient {
    public static void init() {
        ClientConfig.init();
        ClientRegistry.init();
        RegHelper.registerDynamicResourceProvider(ClientDynamicResourcesGenerator.getInstance());

        //? if fabric {
        HudRenderCallback.EVENT.register(CondimentsHud::render);
        //?} elif neoforge {
        /*NeoForge.EVENT_BUS.addListener(CondimentsClient::renderHud);
        *///?}
    }

    //? if neoforge {
    /*public static void renderHud(RenderGuiEvent.Post event) {
        CondimentsHud.render(event.getGuiGraphics(), event.getPartialTick());
    }
    *///?}
}
