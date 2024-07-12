package dev.chililisoup.condiments.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import dev.chililisoup.condiments.block.CondimentsRail;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin {
    @Unique
    private Pair<Vec3i, Vec3i> condiments$getExits(RailShape shape, BlockState state, Vec3 deltaMovement, Operation<Pair<Vec3i, Vec3i>> original) {
        if (state.getBlock() instanceof CondimentsRail rail)
            return rail.getExits(shape, state, deltaMovement, original);
        else return original.call(shape);
    }

    @WrapOperation(method = "moveAlongTrack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/AbstractMinecart;exits(Lnet/minecraft/world/level/block/state/properties/RailShape;)Lcom/mojang/datafixers/util/Pair;"))
    private Pair<Vec3i, Vec3i> moveAlongTrackExits(RailShape shape, Operation<Pair<Vec3i, Vec3i>> original, @Local(argsOnly = true) BlockState state, @Local(ordinal = 1) Vec3 deltaMovement) {
        return condiments$getExits(shape, state, deltaMovement, original);
    }

    @WrapOperation(method = "getPosOffs", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/AbstractMinecart;exits(Lnet/minecraft/world/level/block/state/properties/RailShape;)Lcom/mojang/datafixers/util/Pair;"))
    private Pair<Vec3i, Vec3i> getPosOffsExitsRedirect(RailShape shape, Operation<Pair<Vec3i, Vec3i>> original, @Local BlockState state) {
        Vec3 deltaMovement = ((AbstractMinecart) (Object) this).getDeltaMovement();
        return condiments$getExits(shape, state, deltaMovement, original);
    }

    @WrapOperation(method = "getPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/AbstractMinecart;exits(Lnet/minecraft/world/level/block/state/properties/RailShape;)Lcom/mojang/datafixers/util/Pair;"))
    private Pair<Vec3i, Vec3i> getPosExitsRedirect(RailShape shape, Operation<Pair<Vec3i, Vec3i>> original, @Local BlockState state) {
        Vec3 deltaMovement = ((AbstractMinecart) (Object) this).getDeltaMovement();
        return condiments$getExits(shape, state, deltaMovement, original);
    }

    @Inject(method = "moveAlongTrack", at = @At("TAIL"))
    private void moveAlongTrackHook(BlockPos pos, BlockState state, CallbackInfo ci, @Local RailShape railShape) {
        if (state.getBlock() instanceof CondimentsRail rail)
            rail.moveAlongTrack(pos, state, railShape, (AbstractMinecart) (Object) this);
    }

//    @WrapOperation(method = "moveAlongTrack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/AbstractMinecart;applyNaturalSlowdown()V"))
//    private void applyNaturalSlowdownHook(AbstractMinecart cart, Operation<Void> original, @Local(argsOnly = true) BlockState state) {
//        if (state.getBlock() instanceof CondimentsRail rail) {
//            double multiplier = rail.slowdownMultiplier(cart, state);
//
//            if (multiplier == 0.0) return;
//            if (multiplier == 1.0) {
//                original.call(cart);
//                return;
//            }
//
//            cart.setDeltaMovement(cart.getDeltaMovement().scale(
//            (1 - (cart.isVehicle() ? 0.003 : 0.04) * multiplier)
//                * (cart.isInWater() ? 1 - (0.05 * multiplier) : 1)
//            ));
//            return;
//        }
//
//        original.call(cart);
//    }
}
