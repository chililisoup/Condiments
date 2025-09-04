package dev.chililisoup.condiments.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.chililisoup.condiments.block.CondimentsRail;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin {
    @Inject(method = "moveAlongTrack", at = @At("TAIL"))
    private void moveAlongTrackHook(BlockPos pos, BlockState state, CallbackInfo ci, @Local RailShape railShape) {
        if (state.getBlock() instanceof CondimentsRail rail)
            rail.moveAlongTrack(pos, state, railShape, (AbstractMinecart) (Object) this);
    }
}
