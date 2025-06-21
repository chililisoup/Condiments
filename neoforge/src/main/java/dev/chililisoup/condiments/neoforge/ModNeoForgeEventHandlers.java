package dev.chililisoup.condiments.neoforge;

import dev.chililisoup.condiments.block.IDestroyPreventable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class ModNeoForgeEventHandlers {
    @SubscribeEvent
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        Level level = player.level();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof IDestroyPreventable block)
            event.setCanceled(block.shouldCancelDestroy(state, level, pos, player, event.getFace()));
    }
}
