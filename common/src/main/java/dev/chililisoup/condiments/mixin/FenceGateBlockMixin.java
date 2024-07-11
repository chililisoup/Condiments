package dev.chililisoup.condiments.mixin;

import dev.chililisoup.condiments.reg.ModBlockTags;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FenceGateBlock.class)
public abstract class FenceGateBlockMixin {
    @Inject(method = "isWall", at = @At("RETURN"), cancellable = true)
    private void connectsToInject(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (state.is(ModBlockTags.WOOD_WALLS)) cir.setReturnValue(true);
    }
}
