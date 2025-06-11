package dev.chililisoup.condiments.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.client.renderer.CondimentsHud;
import dev.chililisoup.condiments.client.renderer.CrateItemRenderer;
import dev.chililisoup.condiments.client.renderer.CrateRenderer;
import dev.chililisoup.condiments.item.tooltip.ClientCrateTooltip;
import dev.chililisoup.condiments.item.tooltip.CrateTooltip;
import dev.chililisoup.condiments.reg.ModBlockEntities;
import dev.chililisoup.condiments.reg.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

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

        ItemBlockEntityRenderExtension crateItemRenderer = new ItemBlockEntityRenderExtension(new CrateItemRenderer());
        for (Block block : ModBlocks.getCrates()) {
            BuiltinItemRendererRegistry.INSTANCE.register(block.asItem(), crateItemRenderer);
        }

        TooltipComponentCallback.EVENT.register(data -> {
            if (data instanceof CrateTooltip) return new ClientCrateTooltip((CrateTooltip) data);
            return null;
        });

        HudRenderCallback.EVENT.register(CondimentsHud::render);
    }

    private record ItemBlockEntityRenderExtension(BlockEntityWithoutLevelRenderer renderer) implements BuiltinItemRendererRegistry.DynamicItemRenderer {
        @Override
        public void render(ItemStack stack, ItemDisplayContext transform, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
            renderer.renderByItem(stack, transform, poseStack, buffer, light, overlay);
        }
    }
}
