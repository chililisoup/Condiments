//? if fabric {
package dev.chililisoup.condiments.loaders.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.chililisoup.condiments.block.IDestroyPreventable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeMixin {
    @Shadow protected ServerLevel level;
    @Final @Shadow protected ServerPlayer player;

    @WrapMethod(method = "handleBlockBreakAction")
    private void shouldDestroyBlock(BlockPos pos, ServerboundPlayerActionPacket.Action action, Direction face, int maxBuildHeight, int sequence, Operation<Void> original) {
        BlockState state = this.level.getBlockState(pos);

        if (state.getBlock() instanceof IDestroyPreventable block)
            if (block.shouldCancelDestroy(state, level, pos, this.player, face)) return;

        original.call(pos, action, face, maxBuildHeight, sequence);
    }
}
//?}