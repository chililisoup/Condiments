package dev.chililisoup.condiments.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public interface IDestroyPreventable {
    boolean shouldCancelDestroy(BlockState state, Level level, BlockPos pos, Player player, Direction face);

    default boolean shouldPreventAttackRetrigger(BlockState state, Level level, BlockPos pos, Player player, Direction face, BlockHitResult hitResult) {
        return false;
    }
}
