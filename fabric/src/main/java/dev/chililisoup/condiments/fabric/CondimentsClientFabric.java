package dev.chililisoup.condiments.fabric;

import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.client.renderer.CrateRenderer;
import dev.chililisoup.condiments.item.Tooltip.ClientCrateTooltip;
import dev.chililisoup.condiments.item.Tooltip.CrateTooltip;
import dev.chililisoup.condiments.reg.ModBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

import java.util.Objects;

import static dev.chililisoup.condiments.reg.fabric.ModBlocksImpl.BlocksRegistry;

@Environment(EnvType.CLIENT)
public class CondimentsClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Condiments.initClient();

        BlocksRegistry.forEach(reg -> {
            if (Objects.equals(reg.renderType, "CUTOUT")) BlockRenderLayerMap.INSTANCE.putBlock(reg.block, RenderType.cutout());
        });

        BlockEntityRenderers.register(ModBlockEntities.CRATE_BE_TYPE.get(), CrateRenderer::new);

        TooltipComponentCallback.EVENT.register(data -> {
            if (data instanceof CrateTooltip) return new ClientCrateTooltip((CrateTooltip) data);
            return null;
        });
    }
}
