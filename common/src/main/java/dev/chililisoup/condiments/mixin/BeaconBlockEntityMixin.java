package dev.chililisoup.condiments.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import dev.chililisoup.condiments.config.CommonConfig;
import dev.chililisoup.condiments.inject.BeaconBlockEntityInject;
import dev.chililisoup.condiments.reg.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BeaconBlockEntity.class)
public abstract class BeaconBlockEntityMixin implements BeaconBlockEntityInject {
    @Unique private int condiments$topY;

    @Override
    public int condiments$getTopY() {
        return this.condiments$topY;
    }

    @Override
    public void condiments$setTopY(int topY) {
        this.condiments$topY = topY;
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getHeight(Lnet/minecraft/world/level/levelgen/Heightmap$Types;II)I"))
    private static int updateTopY(
            Level level,
            Heightmap.Types heightmapType,
            int x,
            int z,
            Operation<Integer> original,
            @Local(argsOnly = true) BeaconBlockEntity blockEntity
    ) {
        blockEntity.condiments$setTopY(level.getHeight());
        return original.call(level, heightmapType, x, z);
    }

    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z"))
    private static boolean allowTintedGlass(
            boolean original,
            @Local(argsOnly = true) BeaconBlockEntity blockEntity,
            @Local(ordinal = 1) BlockState blockState,
            @Local(ordinal = 1) BlockPos blockPos,
            @Local(ordinal = 3) LocalIntRef levelHeight
    ) {
        if (original) return true;
        if (!blockState.is(ModBlockTags.TINTED_GLASS)) return false;
        if (!CommonConfig.TINTED_GLASS_TERMINATES_BEACONS.get()) return false;

        int topY = blockPos.getY() - 1;

        blockEntity.condiments$setTopY(topY);
        levelHeight.set(topY);
        return true;
    }

    @WrapOperation(method = "applyEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getHeight()I"))
    private static int adjustEffectRange(Level level, Operation<Integer> original, @Local(argsOnly = true) BlockPos blockPos) {
        if (level.getBlockEntity(blockPos) instanceof BeaconBlockEntity beaconBlockEntity)
            return beaconBlockEntity.condiments$getTopY();

        return original.call(level);
    }
}
