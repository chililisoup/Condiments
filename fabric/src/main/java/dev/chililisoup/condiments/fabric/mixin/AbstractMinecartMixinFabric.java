package dev.chililisoup.condiments.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.chililisoup.condiments.block.CondimentsRail;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixinFabric {
    @WrapOperation(method = "moveAlongTrack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/AbstractMinecart;getMaxSpeed()D"))
    private double maxSpeedHook(AbstractMinecart cart, Operation<Double> original, @Local(argsOnly = true) BlockState state) {
        if (state.getBlock() instanceof CondimentsRail rail)
            return rail.getMaxSpeed(cart);
        else return original.call(cart);
    }
}
