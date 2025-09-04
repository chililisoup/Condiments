//? if fabric {
package dev.chililisoup.condiments.loaders.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import dev.chililisoup.condiments.block.IDestroyPreventable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
    @Final @Shadow private Minecraft minecraft;

    @Unique private boolean condiments$shouldDestroyBlock(MultiPlayerGameMode instance, BlockPos pos, Direction face) {
        Level level = this.minecraft.level;
        if (level == null) return true;

        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof IDestroyPreventable block)
            return !block.shouldCancelDestroy(state, level, pos, this.minecraft.player, face);

        return true;
    }

    @WrapWithCondition(
            method = "method_41936", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;destroyBlock(Lnet/minecraft/core/BlockPos;)Z",
            ordinal = 0
    ))
    private boolean shouldDestroyBlock(MultiPlayerGameMode instance, BlockPos pos, @Local(argsOnly = true) Direction face) {
        return condiments$shouldDestroyBlock(instance, pos, face);
    }

    @WrapWithCondition(
            method = "method_41935", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;destroyBlock(Lnet/minecraft/core/BlockPos;)Z",
            ordinal = 0
    ))
    private boolean shouldContinueDestroyingBlock(MultiPlayerGameMode instance, BlockPos pos, @Local(argsOnly = true) Direction face) {
        return condiments$shouldDestroyBlock(instance, pos, face);
    }
}
//?}