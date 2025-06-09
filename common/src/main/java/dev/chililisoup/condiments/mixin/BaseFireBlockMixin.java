package dev.chililisoup.condiments.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.chililisoup.condiments.block.CopperFireBlock;
import dev.chililisoup.condiments.reg.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BaseFireBlock.class)
public abstract class BaseFireBlockMixin {
    @Inject(method = "getState", at = @At("RETURN"), cancellable = true)
    private static void setCopperFire(BlockGetter reader, BlockPos pos, CallbackInfoReturnable<BlockState> cir, @Local BlockState blockState) {
        if (CopperFireBlock.canSurviveOnBlock(blockState))
            cir.setReturnValue(ModBlocks.COPPER_FIRE.get().defaultBlockState());
    }
}
