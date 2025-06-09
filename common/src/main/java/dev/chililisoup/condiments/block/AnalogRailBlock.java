package dev.chililisoup.condiments.block;

import com.mojang.serialization.MapCodec;
import dev.architectury.injectables.annotations.PlatformOnly;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class AnalogRailBlock extends BaseRailBlock implements CondimentsRail {
    public static final MapCodec<AnalogRailBlock> CODEC = simpleCodec(AnalogRailBlock::new);
    public static final EnumProperty<RailShape> SHAPE;
    public static final IntegerProperty POWER;

    public AnalogRailBlock(Properties properties) {
        super(true, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(SHAPE, RailShape.NORTH_SOUTH).setValue(POWER, 0).setValue(WATERLOGGED, false));
    }

    protected int findAnalogRailSignal(Level level, BlockPos pos, BlockState state, boolean searchForward, int recursionCount) {
        if (recursionCount >= 8) return 0;

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        boolean ascending = true;
        RailShape railShape = state.getValue(SHAPE);
        switch (railShape) {
            case NORTH_SOUTH:
                if (searchForward) ++z;
                else --z;
                ascending = false;
                break;
            case EAST_WEST:
                if (searchForward) --x;
                else ++x;
                ascending = false;
                break;
            case ASCENDING_EAST:
                if (searchForward) --x;
                else {
                    ++x;
                    ++y;
                }
                railShape = RailShape.EAST_WEST;
                break;
            case ASCENDING_WEST:
                if (searchForward) {
                    --x;
                    ++y;
                } else ++x;
                railShape = RailShape.EAST_WEST;
                break;
            case ASCENDING_NORTH:
                if (searchForward) ++z;
                else {
                    --z;
                    ++y;
                }
                railShape = RailShape.NORTH_SOUTH;
                break;
            case ASCENDING_SOUTH:
                if (searchForward) {
                    ++z;
                    ++y;
                } else --z;
                railShape = RailShape.NORTH_SOUTH;
        }

        int flatSignal = this.getConnectedPower(level, new BlockPos(x, y, z), searchForward, recursionCount, railShape);
        if (flatSignal > 0) return flatSignal;

        if (ascending) return 0;

        return this.getConnectedPower(level, new BlockPos(x, y - 1, z), searchForward, recursionCount, railShape);
    }

    protected int getConnectedPower(Level level, BlockPos pos, boolean searchForward, int recursionCount, RailShape shape) {
        BlockState blockState = level.getBlockState(pos);
        if (!blockState.is(this)) return 0;

        RailShape railShape = blockState.getValue(SHAPE);
        if (shape == RailShape.EAST_WEST && (railShape == RailShape.NORTH_SOUTH || railShape == RailShape.ASCENDING_NORTH || railShape == RailShape.ASCENDING_SOUTH))
            return 0;

        if (shape == RailShape.NORTH_SOUTH && (railShape == RailShape.EAST_WEST || railShape == RailShape.ASCENDING_EAST || railShape == RailShape.ASCENDING_WEST))
            return 0;

        int power = blockState.getValue(POWER);
        if (power > 0) {
            int signal = level.getBestNeighborSignal(pos);
            if (signal > 14) return signal;

            return Math.max(this.findAnalogRailSignal(level, pos, blockState, searchForward, recursionCount + 1), signal);
        }

        return 0;
    }

    @Override
    protected @NotNull MapCodec<? extends BaseRailBlock> codec() {
        return CODEC;
    }

    @Override
    protected void updateState(BlockState state, Level level, BlockPos pos, Block neighborBlock) {
        int power = state.getValue(POWER);
        int signal = level.getBestNeighborSignal(pos);

        if (signal < 15) {
            int forward = this.findAnalogRailSignal(level, pos, state, true, 0);
            if (forward > signal) signal = forward;

            if (signal < 15) {
                int backward = this.findAnalogRailSignal(level, pos, state, false, 0);
                if (backward > signal) signal = backward;
            }
        }

        if (signal == power) return;

        level.setBlock(pos, state.setValue(POWER, signal), 3);
        level.updateNeighborsAt(pos.below(), this);
        if (state.getValue(SHAPE).isAscending())
            level.updateNeighborsAt(pos.above(), this);
    }

    @Override
    public @NotNull Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return state.getValue(POWER);
    }

    public Property<Integer> getPowerProperty() {
        return POWER;
    }

    @Override
    public double getMaxSpeed(AbstractMinecart cart) {
        return (cart.isInWater() ? 0.3 : 0.6);
    }

    @Override
    public void moveAlongTrack(BlockPos pos, BlockState state, RailShape railShape, AbstractMinecart cart) {
        Vec3 movement = cart.getDeltaMovement();
        double x = movement.x;
        double z = movement.z;
        double horizontalDistance = movement.horizontalDistance();
        int power = state.getValue(((AnalogRailBlock) state.getBlock()).getPowerProperty());

        if (power == 0) {
            if (horizontalDistance < 0.03)
                cart.setDeltaMovement(Vec3.ZERO);
            else
                cart.setDeltaMovement(movement.multiply(0.5, 0.0, 0.5));
            return;
        }

        if (horizontalDistance > 0.01) {
            double speed = power * getMaxSpeed(cart) / 15.0;

            if (x > 0.01) x = speed;
            else if (x < -0.01) x = -speed;

            if (z > 0.01) z = speed;
            else if (z < -0.01) z = -speed;

            cart.setDeltaMovement(x, 0.0, z);
            return;
        }

        if (railShape == RailShape.EAST_WEST) {
            if (isRedstoneConductor(pos.west(), cart)) x = 0.02;
            else if (isRedstoneConductor(pos.east(), cart)) x = -0.02;
        } else {
            if (railShape != RailShape.NORTH_SOUTH) return;

            if (isRedstoneConductor(pos.north(), cart)) z = 0.02;
            else if (isRedstoneConductor(pos.south(), cart)) z = -0.02;
        }

        cart.setDeltaMovement(x, movement.y, z);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SHAPE, POWER, WATERLOGGED);
    }

    static {
        SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
        POWER = BlockStateProperties.POWER;
    }

    @PlatformOnly("neoforge")
    public float getRailMaxSpeed(BlockState state, Level level, BlockPos pos, AbstractMinecart cart) {
        return (float) getMaxSpeed(cart);
    }
}
