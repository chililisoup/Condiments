package dev.chililisoup.condiments.neoforge;

import dev.chililisoup.condiments.block.IDestroyPreventable;
import dev.chililisoup.condiments.block.entity.neoforge.CrateBlockEntityImpl;
import dev.chililisoup.condiments.reg.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber
public class ModNeoForgeEventHandlers {
    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        Level level = player.level();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof IDestroyPreventable block)
            event.setCanceled(block.shouldCancelDestroy(state, level, pos, player, event.getFace()));
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.CRATE_BE_TYPE.get(),
                (crateBlockEntity, context) -> ((CrateBlockEntityImpl) crateBlockEntity).handler
        );
    }
}
