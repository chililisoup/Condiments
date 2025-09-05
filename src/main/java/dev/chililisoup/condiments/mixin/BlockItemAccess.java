package dev.chililisoup.condiments.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@SuppressWarnings("UnusedReturnValue")
@Mixin(BlockItem.class)
public interface BlockItemAccess {
    @Invoker("getPlacementState")
    BlockState condiments$getPlacementState(BlockPlaceContext context);

    @Invoker("updateBlockStateFromTag")
    BlockState condiments$updateBlockStateFromTag(BlockPos pos, Level level, ItemStack stack, BlockState state);

    @Invoker("updateCustomBlockEntityTag")
    boolean condiments$updateCustomBlockEntityTag(BlockPos pos, Level level, @Nullable Player player, ItemStack stack, BlockState state);

    @Invoker("updateBlockEntityComponents")
    static void condiments$updateBlockEntityComponents(Level level, BlockPos poa, ItemStack stack) {
        throw new AssertionError();
    }

    @Invoker("getPlaceSound")
    SoundEvent condiments$getPlaceSound(BlockState state);
}
