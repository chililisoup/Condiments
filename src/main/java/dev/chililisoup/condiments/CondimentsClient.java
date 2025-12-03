package dev.chililisoup.condiments;

import dev.chililisoup.condiments.client.renderer.CondimentsHud;
import dev.chililisoup.condiments.config.ClientConfig;
import dev.chililisoup.condiments.dynamicpack.ClientDynamicResourcesGenerator;
import dev.chililisoup.condiments.reg.ClientRegistry;

//? if >= 1.21 {
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
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

//? if fabric && < 1.21
/*import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;*/

//$ client_only
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class CondimentsClient {
    //? if fabric && < 1.21
    /*public static boolean CREATE_LOADED;*/

    public static void init() {
        ClientConfig.init();
        ClientRegistry.init();

        //? if < 1.21 {
        /*ClientDynamicResourcesGenerator.INSTANCE.register();
        *///?} else {
        RegHelper.registerDynamicResourceProvider(new ClientDynamicResourcesGenerator());
         //?}

        //? if fabric {
        HudRenderCallback.EVENT.register(CondimentsHud::render);
        //?} elif neoforge {
        /*NeoForge.EVENT_BUS.addListener(CondimentsClient::renderHud);
        *///?} elif forge {
        /*MinecraftForge.EVENT_BUS.addListener(CondimentsClient::renderHud);
        *///?}
    }

    //? if forge_like {
    /*public static void renderHud(RenderGuiEvent.Post event) {
        CondimentsHud.render(event.getGuiGraphics(), event.getPartialTick());
    }
    *///?}

    //? if fabric && < 1.21 {
    /*static {
        CREATE_LOADED = PlatHelper.isModLoaded("create");
    }
    *///?}
}
