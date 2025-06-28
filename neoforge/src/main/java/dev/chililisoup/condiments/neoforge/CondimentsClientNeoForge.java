package dev.chililisoup.condiments.neoforge;

import dev.chililisoup.condiments.client.renderer.CondimentsHud;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;

public class CondimentsClientNeoForge {
    public static void init() {
        NeoForge.EVENT_BUS.addListener(CondimentsClientNeoForge::renderHud);
    }

    public static void renderHud(RenderGuiEvent.Post event) {
        CondimentsHud.render(event.getGuiGraphics(), event.getPartialTick());
    }
}
