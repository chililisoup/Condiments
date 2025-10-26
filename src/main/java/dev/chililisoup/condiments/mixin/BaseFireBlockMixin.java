package dev.chililisoup.condiments.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.chililisoup.condiments.block.CopperFireBlock;
import dev.chililisoup.condiments.config.CommonConfig;
import dev.chililisoup.condiments.reg.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BaseFireBlock.class)
public abstract class BaseFireBlockMixin {
    @ModifyReturnValue(method = "getState", at = @At("RETURN"))
    private static BlockState setCopperFire(
            BlockState original,
            @Local(argsOnly = true) BlockGetter reader,
            @Local(argsOnly = true) BlockPos pos,
            @Local BlockState blockState
    ) {
        if (!(original.getBlock() instanceof FireBlock)) return original;
        if (!CommonConfig.COPPER_FIRE.get()) return original;
        if (!CopperFireBlock.canSurviveOnBlock(blockState, reader, pos.below())) return original;
        return ModBlocks.COPPER_FIRE.get().defaultBlockState();
    }
}
