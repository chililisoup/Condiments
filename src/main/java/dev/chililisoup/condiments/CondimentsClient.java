package dev.chililisoup.condiments;

import dev.chililisoup.condiments.client.renderer.CondimentsHud;
import dev.chililisoup.condiments.config.ClientConfig;
import dev.chililisoup.condiments.reg.ClientRegistry;

//? if < 1.21 {
/*import dev.chililisoup.condiments.dynamicpack.ClientDynamicResourcesGeneratorOld;
*///?} else {
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import dev.chililisoup.condiments.dynamicpack.ClientDynamicResourcesGenerator;
//?}

//? if fabric {
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
//?} elif neoforge {
/*import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;
*///?} elif forge {
/*import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.common.MinecraftForge;
*///?}

//$ client_only
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class CondimentsClient {
    public static void init() {
        ClientConfig.init();
        ClientRegistry.init();

        //? if < 1.21 {
        /*ClientDynamicResourcesGeneratorOld.INSTANCE.register();
        *///?} else {
        RegHelper.registerDynamicResourceProvider(ClientDynamicResourcesGenerator.getInstance());
         //?}

        //? if fabric {
        HudRenderCallback.EVENT.register(CondimentsHud::render);
        //?} elif neoforge {
        /*NeoForge.EVENT_BUS.addListener(CondimentsClient::renderHud);
        *///?} elif forge {
        /*MinecraftForge.EVENT_BUS.addListener(CondimentsClient::renderHud);
        *///?}
    }

    //? if forgeLike {
    /*public static void renderHud(RenderGuiEvent.Post event) {
        CondimentsHud.render(event.getGuiGraphics(), event.getPartialTick());
    }
    *///?}
}
