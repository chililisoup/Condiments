package dev.chililisoup.condiments.reg;

import dev.chililisoup.condiments.block.AnalogRailBlock;
import dev.chililisoup.condiments.block.RedstoneLedBlock;
import dev.chililisoup.condiments.client.renderer.CrateItemRenderer;
import dev.chililisoup.condiments.client.renderer.CrateRenderer;
import dev.chililisoup.condiments.item.tooltip.ClientCrateTooltip;
import dev.chililisoup.condiments.item.tooltip.CrateTooltip;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mehvahdjukaar.moonlight.api.platform.ClientHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedStoneWireBlock;

@Environment(EnvType.CLIENT)
public class ClientRegistry {
    public static void init() {
        ClientHelper.addClientSetup(ClientRegistry::setup);

        ClientHelper.addBlockEntityRenderersRegistration(ClientRegistry::registerBlockEntityRenderers);
        ClientHelper.addItemRenderersRegistration(ClientRegistry::registerItemRenderers);
        ClientHelper.addTooltipComponentRegistration(ClientRegistry::registerTooltipComponents);
        ClientHelper.addBlockColorsRegistration(ClientRegistry::registerBlockColors);
    }

    private static void setup() {
        ModBlocks.ALT_RENDERED_BLOCKS.forEach((block, renderType) -> {
            if (renderType.equals("CUTOUT"))
                ClientHelper.registerRenderType(block.get(), RenderType.cutout());
        });
    }

    private static void registerBlockEntityRenderers(ClientHelper.BlockEntityRendererEvent event) {
        event.register(ModBlockEntities.CRATE_BE_TYPE.get(), CrateRenderer::new);
    }

    private static void registerItemRenderers(ClientHelper.ItemRendererEvent event) {
        for (Block block : ModBlocks.getCrates())
            event.register(block, new CrateItemRenderer());
    }

    private static void registerTooltipComponents(ClientHelper.TooltipComponentEvent event) {
        event.register(CrateTooltip.class, ClientCrateTooltip::new);
    }

    private static void registerBlockColors(ClientHelper.BlockColorEvent event) {
        event.register(
                (state, view, pos, tintIndex) ->
                        RedStoneWireBlock.getColorForPower(state.getValue(((AnalogRailBlock) state.getBlock()).getPowerProperty())),
                ModBlocks.ANALOG_RAIL.get()
        );

        event.register(
                (state, view, pos, tintIndex) ->
                        RedstoneLedBlock.getColorForPower(state.getValue(((RedstoneLedBlock) state.getBlock()).getPowerProperty())),
                ModBlocks.REDSTONE_LED.get()
        );
    }
}
