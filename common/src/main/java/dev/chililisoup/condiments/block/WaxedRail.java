//package dev.chililisoup.condiments.block;
//
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.entity.vehicle.AbstractMinecart;
//import net.minecraft.world.level.block.BaseRailBlock;
//import net.minecraft.world.level.block.RailBlock;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.block.state.properties.RailShape;
//import net.minecraft.world.phys.Vec3;
//
//public class WaxedRail extends RailBlock implements CondimentsRail {
//    public WaxedRail(Properties properties) {
//        super(properties);
//    }
//
//    @Override
//    public double getMaxSpeed(AbstractMinecart cart) {
//        return (cart.isInWater() ? 0.2 : 0.8);
//    }
//
//    @Override
//    public void moveAlongTrack(BlockPos pos, BlockState state, RailShape railShape, AbstractMinecart cart) {
//        Vec3 movement = cart.getDeltaMovement();
//
//        if (movement.y > 0) return;
//        if (railShape == RailShape.EAST_WEST || railShape == RailShape.NORTH_SOUTH)
//            cart.setDeltaMovement(movement.x, 0, movement.z);
//        else cart.setDeltaMovement(movement.x, -movement.horizontalDistance(), movement.z);
//    }
//
//    @Override
//    public double slowdownMultiplier(AbstractMinecart cart, BlockState state) {
//        RailShape railShape = state.getValue(((BaseRailBlock) state.getBlock()).getShapeProperty());
//        return railShape == RailShape.EAST_WEST || railShape == RailShape.NORTH_SOUTH ? 0.05 : 0.5;
//    }
//}
