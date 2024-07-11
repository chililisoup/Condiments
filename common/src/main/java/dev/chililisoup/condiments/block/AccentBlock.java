package dev.chililisoup.condiments.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AccentBlock extends Block implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING;
    public static final EnumProperty<Half> HALF;
    public static final EnumProperty<StairsShape> SHAPE;
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape[] TOP_SHAPES;
    protected static final VoxelShape[] BOTTOM_SHAPES;
    private static final int[] SHAPE_BY_STATE;

    public AccentBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HALF, Half.BOTTOM).setValue(SHAPE, StairsShape.STRAIGHT).setValue(WATERLOGGED, false));
    }

    public boolean useShapeForLightOcclusion(BlockState state) {
        return false;
    }

    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return (state.getValue(HALF) == Half.TOP ? TOP_SHAPES : BOTTOM_SHAPES)[SHAPE_BY_STATE[this.getShapeIndex(state)]];
    }

    private int getShapeIndex(BlockState state) {
        return (state.getValue(SHAPE)).ordinal() * 4 + (state.getValue(FACING)).get2DDataValue();
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getClickedFace();
        BlockPos blockPos = context.getClickedPos();
        FluidState fluidState = context.getLevel().getFluidState(blockPos);
        BlockState blockState = this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(HALF, direction != Direction.DOWN && (direction == Direction.UP || !(context.getClickLocation().y - (double)blockPos.getY() > 0.5)) ? Half.BOTTOM : Half.TOP).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
        return blockState.setValue(SHAPE, getStairsShape(blockState, context.getLevel(), blockPos));
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return direction.getAxis().isHorizontal() ? state.setValue(SHAPE, getStairsShape(state, level, pos)) : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    private static StairsShape getStairsShape(BlockState state, BlockGetter level, BlockPos pos) {
        Direction direction = state.getValue(FACING);
        BlockState blockState = level.getBlockState(pos.relative(direction));
        if (isAccent(blockState) && state.getValue(HALF) == blockState.getValue(HALF)) {
            Direction direction2 = blockState.getValue(FACING);
            if (direction2.getAxis() != state.getValue(FACING).getAxis() && canTakeShape(state, level, pos, direction2.getOpposite())) {
                if (direction2 == direction.getCounterClockWise()) return StairsShape.OUTER_LEFT;
                return StairsShape.OUTER_RIGHT;
            }
        }
        BlockState blockState2 = level.getBlockState(pos.relative(direction.getOpposite()));
        if (isAccent(blockState2) && state.getValue(HALF) == blockState2.getValue(HALF)) {
            Direction direction3 = blockState2.getValue(FACING);
            if (direction3.getAxis() != state.getValue(FACING).getAxis() && canTakeShape(state, level, pos, direction3)) {
                if (direction3 == direction.getCounterClockWise()) return StairsShape.INNER_LEFT;
                return StairsShape.INNER_RIGHT;
            }
        }
        return StairsShape.STRAIGHT;
    }

    private static boolean canTakeShape(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        BlockState blockState = level.getBlockState(pos.relative(face));
        return !isAccent(blockState) || blockState.getValue(FACING) != state.getValue(FACING) || blockState.getValue(HALF) != state.getValue(HALF);
    }

    public static boolean isAccent(BlockState state) {
        return state.getBlock() instanceof AccentBlock;
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        Direction direction = state.getValue(FACING);
        StairsShape stairsShape = state.getValue(SHAPE);
        switch (mirror) {
            case LEFT_RIGHT:
                if (direction.getAxis() == Direction.Axis.Z) {
                    return switch (stairsShape) {
                        case INNER_LEFT -> state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_RIGHT);
                        case INNER_RIGHT -> state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_LEFT);
                        case OUTER_LEFT -> state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_RIGHT);
                        case OUTER_RIGHT -> state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_LEFT);
                        default -> state.rotate(Rotation.CLOCKWISE_180);
                    };
                }
            case FRONT_BACK:
                if (direction.getAxis() == Direction.Axis.X) {
                    return switch (stairsShape) {
                        case INNER_LEFT -> state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_LEFT);
                        case INNER_RIGHT -> state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.INNER_RIGHT);
                        case OUTER_LEFT -> state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_RIGHT);
                        case OUTER_RIGHT -> state.rotate(Rotation.CLOCKWISE_180).setValue(SHAPE, StairsShape.OUTER_LEFT);
                        case STRAIGHT -> state.rotate(Rotation.CLOCKWISE_180);
                    };
                }
        }

        return super.mirror(state, mirror);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF, SHAPE, WATERLOGGED);
    }

    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
        return false;
    }

    static {
        FACING = HorizontalDirectionalBlock.FACING;
        HALF = BlockStateProperties.HALF;
        SHAPE = BlockStateProperties.STAIRS_SHAPE;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;

        VoxelShape SIDE_BN = Block.box(0, 0, 0, 16, 3, 3);
        VoxelShape SIDE_BS = Block.box(0, 0, 13, 16, 3, 16);
        VoxelShape SIDE_BW = Block.box(0, 0, 0, 3, 3, 16);
        VoxelShape SIDE_BE = Block.box(13, 0, 0, 16, 3, 16);

        VoxelShape SIDE_TN = Block.box(0, 13, 0, 16, 16, 3);
        VoxelShape SIDE_TS = Block.box(0, 13, 13, 16, 16, 16);
        VoxelShape SIDE_TW = Block.box(0, 13, 0, 3, 16, 16);
        VoxelShape SIDE_TE = Block.box(13, 13, 0, 16, 16, 16);

        VoxelShape COR_BNW = Block.box(0, 0, 0, 3, 3, 3);
        VoxelShape COR_BNE = Block.box(13, 0, 0, 16, 3, 3);
        VoxelShape COR_BSW = Block.box(0, 0, 13, 3, 3, 16);
        VoxelShape COR_BSE = Block.box(13, 0, 13, 16, 3, 16);

        VoxelShape COR_TNW = Block.box(0, 13, 0, 3, 16, 3);
        VoxelShape COR_TNE = Block.box(13, 13, 0, 16, 16, 3);
        VoxelShape COR_TSW = Block.box(0, 13, 13, 3, 16, 16);
        VoxelShape COR_TSE = Block.box(13, 13, 13, 16, 16, 16);

        TOP_SHAPES = new VoxelShape[]{
                SIDE_TS,
                SIDE_TW,
                SIDE_TN,
                SIDE_TE,

                Shapes.or(SIDE_TS, SIDE_TE),
                Shapes.or(SIDE_TW, SIDE_TS),
                Shapes.or(SIDE_TN, SIDE_TW),
                Shapes.or(SIDE_TE, SIDE_TN),

                COR_TSE,
                COR_TSW,
                COR_TNW,
                COR_TNE
        };
        BOTTOM_SHAPES = new VoxelShape[]{
                SIDE_BS,
                SIDE_BW,
                SIDE_BN,
                SIDE_BE,

                Shapes.or(SIDE_BS, SIDE_BE),
                Shapes.or(SIDE_BW, SIDE_BS),
                Shapes.or(SIDE_BN, SIDE_BW),
                Shapes.or(SIDE_BE, SIDE_BN),

                COR_BSE,
                COR_BSW,
                COR_BNW,
                COR_BNE
        };
        SHAPE_BY_STATE = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 5, 6, 7, 4, 8, 9, 10, 11, 9, 10, 11, 8};
    }
}
