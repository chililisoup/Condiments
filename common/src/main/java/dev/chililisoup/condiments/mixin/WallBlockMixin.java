package dev.chililisoup.condiments.mixin;

import dev.chililisoup.condiments.reg.ModBlockTags;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WallBlock.class)
public abstract class WallBlockMixin {
    @Inject(method = "connectsTo", at = @At("RETURN"), cancellable = true)
    private void connectsToInject(BlockState state, boolean sideSolid, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (state.is(ModBlockTags.WOOD_WALLS)) cir.setReturnValue(true);
    }
}
