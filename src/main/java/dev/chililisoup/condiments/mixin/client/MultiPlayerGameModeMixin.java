package dev.chililisoup.condiments.mixin.client;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.chililisoup.condiments.block.IDestroyPreventable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
    @Shadow @Final private Minecraft minecraft;
    @Shadow private BlockPos destroyBlockPos = new BlockPos(-1, -1, -1);
    @Shadow private float destroyTicks;

    @WrapOperation(method = "startDestroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;sameDestroyTarget(Lnet/minecraft/core/BlockPos;)Z"))
    private boolean allowStartDestroyRetrigger(
            MultiPlayerGameMode gameMode,
            BlockPos pos,
            Operation<Boolean> original,
            @Local(argsOnly = true) Direction face
    ) {
        if (!original.call(gameMode, pos)) return false;

        if (!pos.equals(this.destroyBlockPos)) return true;

        Level level = this.minecraft.level;
        if (level == null) return true;

        Player player = this.minecraft.player;
        if (player == null) return true;

        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof IDestroyPreventable block)) return true;

        HitResult hitResult = this.minecraft.hitResult;
        if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) return true;

        return !block.shouldPreventAttackRetrigger(state, level, pos, player, face, (BlockHitResult) hitResult);
    }

    @WrapOperation(method = "continueDestroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;sameDestroyTarget(Lnet/minecraft/core/BlockPos;)Z"))
    private boolean preventItemChangeTargetInvalidation(
            MultiPlayerGameMode gameMode,
            BlockPos pos,
            Operation<Boolean> original,
            @Local(argsOnly = true) Direction face
    ) {
        boolean base = original.call(gameMode, pos);
        if (!pos.equals(this.destroyBlockPos)) return base;

        Level level = this.minecraft.level;
        if (level == null) return base;

        Player player = this.minecraft.player;
        if (player == null) return base;

        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof IDestroyPreventable block)) return base;

        HitResult hitResult = this.minecraft.hitResult;
        if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) return base;

        if (block.shouldPreventAttackRetrigger(state, level, pos, player, face, (BlockHitResult) hitResult))
            return (int) this.destroyTicks % 6 != 5;

        return base;
    }

    //? if fabric {
    @Unique
    private boolean condiments$shouldDestroyBlock(BlockPos pos, Direction face) {
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
        return condiments$shouldDestroyBlock(pos, face);
    }

    @WrapWithCondition(
            method = "method_41935", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;destroyBlock(Lnet/minecraft/core/BlockPos;)Z",
            ordinal = 0
    ))
    private boolean shouldContinueDestroyingBlock(MultiPlayerGameMode instance, BlockPos pos, @Local(argsOnly = true) Direction face) {
        return condiments$shouldDestroyBlock(pos, face);
    }
    //?}
}
