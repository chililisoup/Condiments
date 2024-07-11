package dev.chililisoup.condiments.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import dev.chililisoup.condiments.reg.ModBlocks;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin {
    @Shadow
    private static Pair<Vec3i, Vec3i> exits(RailShape shape) {
        return null;
    }

    @Unique
    private Pair<Vec3i, Vec3i> condiments$getExits(RailShape shape, BlockState state, Vec3 deltaMovement) {
        if (state.is(ModBlocks.RAIL_INTERSECTION.get())) {
            if (Math.abs(deltaMovement.z) > Math.abs((deltaMovement.x))) {
                return exits(RailShape.NORTH_SOUTH);
            }
            return exits(RailShape.EAST_WEST);
        } else return exits(shape);
    }

    @Redirect(method = "moveAlongTrack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/AbstractMinecart;exits(Lnet/minecraft/world/level/block/state/properties/RailShape;)Lcom/mojang/datafixers/util/Pair;"))
    private Pair<Vec3i, Vec3i> moveAlongTrackExitsRedirect(RailShape shape, @Local(argsOnly = true) BlockState state, @Local(ordinal = 1) Vec3 deltaMovement) {
        return condiments$getExits(shape, state, deltaMovement);
    }

    @Redirect(method = "getPosOffs", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/AbstractMinecart;exits(Lnet/minecraft/world/level/block/state/properties/RailShape;)Lcom/mojang/datafixers/util/Pair;"))
    private Pair<Vec3i, Vec3i> getPosOffsExitsRedirect(RailShape shape, @Local BlockState state) {
        Vec3 deltaMovement = ((AbstractMinecart)(Object)this).getDeltaMovement();
        return condiments$getExits(shape, state, deltaMovement);
    }

    @Redirect(method = "getPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/AbstractMinecart;exits(Lnet/minecraft/world/level/block/state/properties/RailShape;)Lcom/mojang/datafixers/util/Pair;"))
    private Pair<Vec3i, Vec3i> getPosExitsRedirect(RailShape shape, @Local BlockState state) {
        Vec3 deltaMovement = ((AbstractMinecart)(Object)this).getDeltaMovement();
        return condiments$getExits(shape, state, deltaMovement);
    }
}
