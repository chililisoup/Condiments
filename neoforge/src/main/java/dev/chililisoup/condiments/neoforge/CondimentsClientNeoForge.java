package dev.chililisoup.condiments.neoforge;

import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.client.renderer.CondimentsHud;
import dev.chililisoup.condiments.client.renderer.CrateItemRenderer;
import dev.chililisoup.condiments.client.renderer.CrateRenderer;
import dev.chililisoup.condiments.item.tooltip.ClientCrateTooltip;
import dev.chililisoup.condiments.item.tooltip.CrateTooltip;
import dev.chililisoup.condiments.reg.ModBlockEntities;
import dev.chililisoup.condiments.reg.ModBlocks;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static dev.chililisoup.condiments.reg.neoforge.ModColorProvidersImpl.BLOCK_COLORS;

public class CondimentsClientNeoForge {
    public static void init(IEventBus eventBus) {
        Condiments.initClient();
        eventBus.addListener(CondimentsClientNeoForge::registerEntityRenderers);
        eventBus.addListener(CondimentsClientNeoForge::registerClientTooltips);
        eventBus.addListener(CondimentsClientNeoForge::registerBlockColors);
        eventBus.addListener(CondimentsClientNeoForge::registerClientExtensions);
        NeoForge.EVENT_BUS.addListener(CondimentsClientNeoForge::renderHud);
    }

    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.CRATE_BE_TYPE.get(), CrateRenderer::new);
    }

    public static void registerClientTooltips(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(CrateTooltip.class, ClientCrateTooltip::new);
    }

    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        BLOCK_COLORS.forEach(reg -> event.register(reg.getFirst(), reg.getSecond().get()));
    }

    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(
                new ItemBlockEntityRenderExtension(new CrateItemRenderer()),
                Arrays.stream(ModBlocks.getCrates()).map(Block::asItem).toArray(Item[]::new)
        );
    }

    private record ItemBlockEntityRenderExtension(
            BlockEntityWithoutLevelRenderer renderer) implements IClientItemExtensions {
        @Override
        public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return renderer;
        }
    }

    public static void renderHud(RenderGuiEvent.Post event) {
        CondimentsHud.render(event.getGuiGraphics(), event.getPartialTick());
    }
}
