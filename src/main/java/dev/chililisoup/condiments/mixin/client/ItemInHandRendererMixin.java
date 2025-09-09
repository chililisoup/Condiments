package dev.chililisoup.condiments.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.chililisoup.condiments.block.entity.CrateContents;
import dev.chililisoup.condiments.config.ClientConfig;
import dev.chililisoup.condiments.item.CrateItem;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
    @Unique
    private @Nullable ItemStack getItemOverrideIfNecessary(ItemStack itemStack, AbstractClientPlayer player) {
        if (player.isShiftKeyDown() || !ClientConfig.RENDER_CRATE_CONTENTS_IN_HAND.get() || !(itemStack.getItem() instanceof CrateItem))
            return null;

        CrateContents crateContents = CrateContents.fromCrateItem(itemStack);
        if (crateContents.count() <= 0) return null;

        Optional<CrateContents.ItemRecord> itemRecord = crateContents.itemRecord();
        if (itemRecord.isEmpty()) return null;

        ItemStack contentsStack = itemRecord.get().asItemStack();
        if (contentsStack.getItem() instanceof BlockItem) return contentsStack;
        return null;
    }

    @WrapOperation(
            method = "renderArmWithItem", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            ordinal = 1
    ))
    private void renderCrateContentsInstead(
            ItemInHandRenderer renderer,
            LivingEntity entity,
            ItemStack itemStack,
            ItemDisplayContext displayContext,
            boolean leftHand,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int seed,
            Operation<Void> original,
            @Local(argsOnly = true) AbstractClientPlayer player
    ) {
        ItemStack itemOverride = this.getItemOverrideIfNecessary(itemStack, player);
        ItemStack renderedItem = itemOverride == null ? itemStack : itemOverride;

        original.call(renderer, entity, renderedItem, displayContext, leftHand, poseStack, buffer, seed);
    }
}
