package dev.chililisoup.condiments.mixin;

import dev.chililisoup.condiments.reg.ModBlockTags;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IronBarsBlock.class)
public abstract class IronBarsBlockMixin {
    @Inject(method = "attachsTo", at = @At("RETURN"), cancellable = true)
    private void connectsToInject(BlockState state, boolean solidSide, CallbackInfoReturnable<Boolean> cir) {
        if (state.is(ModBlockTags.WOOD_WALLS)) cir.setReturnValue(true);
    }
}
