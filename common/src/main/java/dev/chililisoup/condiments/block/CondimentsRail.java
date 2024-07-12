package dev.chililisoup.condiments.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;

public interface CondimentsRail {
    default void moveAlongTrack(BlockPos pos, BlockState state, RailShape railShape, AbstractMinecart cart) {}

    default double getMaxSpeed(AbstractMinecart cart) {
        return (cart.isInWater() ? 0.2 : 0.4);
    }

    default boolean isRedstoneConductor(BlockPos pos, AbstractMinecart cart) {
        return cart.level().getBlockState(pos).isRedstoneConductor(cart.level(), pos);
    }

    default Pair<Vec3i, Vec3i> getExits(RailShape shape, BlockState state, Vec3 deltaMovement, Operation<Pair<Vec3i, Vec3i>> original) {
        return original.call(shape);
    }

//    default double slowdownMultiplier(AbstractMinecart cart, BlockState state) {
//        return 1.0;
//    }
}
