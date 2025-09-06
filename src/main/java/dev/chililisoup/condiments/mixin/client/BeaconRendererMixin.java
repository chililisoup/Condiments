package dev.chililisoup.condiments.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.chililisoup.condiments.inject.BeaconBlockEntityInterface;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BeaconRenderer.class)
public abstract class BeaconRendererMixin {
    //? if < 1.21 {
    /*@WrapOperation(
            method = "render(Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/blockentity/BeaconRenderer;renderBeaconBeam(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;FJII[F)V")
    )
    private void adjustBeamHeight(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            float partialTick,
            long gameTime,
            int yOffset,
            int height,
            float[] color,
            Operation<Void> original,
            @Local(argsOnly = true) BeaconBlockEntity blockEntity,
            @Local BeaconBlockEntity.BeaconBeamSection beaconBeamSection
    ) {
    *///?} else {
    @WrapOperation(
            method = "render(Lnet/minecraft/world/level/block/entity/BeaconBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/blockentity/BeaconRenderer;renderBeaconBeam(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;FJIII)V")
    )
    private void adjustBeamHeight(
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            float partialTick,
            long gameTime,
            int yOffset,
            int height,
            int color,
            Operation<Void> original,
            @Local(argsOnly = true) BeaconBlockEntity blockEntity,
            @Local BeaconBlockEntity.BeaconBeamSection beaconBeamSection
    ) {
    //?}
        Level level = blockEntity.getLevel();

        int adjustedHeight = (
                level == null ||
                !(blockEntity instanceof BeaconBlockEntityInterface beacon) ||
                beacon.condiments$getTopY() == level.getHeight()
        ) ?
                height :
                beaconBeamSection.getHeight() - 1;

        original.call(poseStack, bufferSource, partialTick, gameTime, yOffset, adjustedHeight, color);
    }
}
