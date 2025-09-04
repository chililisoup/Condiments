package dev.chililisoup.condiments.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class SaucerLightBlock extends DirectionalBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<SaucerLightBlock> CODEC = simpleCodec(SaucerLightBlock::new);
    public static final BooleanProperty WATERLOGGED;
    public static final BooleanProperty POWERED;
    public static final BooleanProperty LIT;
    private static final Map<Direction, VoxelShape> AABBS;

    @Override
    public @NotNull MapCodec<SaucerLightBlock> codec() {
        return CODEC;
    }

    public SaucerLightBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.UP)
                .setValue(WATERLOGGED, false)
                .setValue(POWERED, false)
                .setValue(LIT, false)
        );
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!player.isShiftKeyDown())
            return InteractionResult.PASS;

        if (level instanceof ServerLevel serverLevel)
            this.checkAndFlip(state, serverLevel, pos, true);

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (oldState.getBlock() != state.getBlock() && level instanceof ServerLevel serverLevel)
            this.checkAndFlip(state, serverLevel, pos);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (level instanceof ServerLevel serverLevel)
            this.checkAndFlip(state, serverLevel, pos);
    }

    public void checkAndFlip(BlockState state, ServerLevel level, BlockPos pos, boolean force) {
        boolean powered = state.getValue(POWERED);

        Direction direction = state.getValue(FACING).getOpposite();
        boolean signal = level.getSignal(pos.relative(direction), direction) > 0;

        if (!force && signal == powered) return;
        BlockState blockState = state;

        if (force || !powered) {
            blockState = state.cycle(LIT);
            level.playSound(null, pos, blockState.getValue(LIT) ? SoundEvents.COPPER_BULB_TURN_ON : SoundEvents.COPPER_BULB_TURN_OFF, SoundSource.BLOCKS);
        }

        level.setBlock(pos, blockState.setValue(POWERED, signal), 3);
    }

    public void checkAndFlip(BlockState state, ServerLevel level, BlockPos pos) {
        this.checkAndFlip(state, level, pos, false);
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AABBS.get(state.getValue(FACING));
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED))
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelAccessor levelAccessor = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        return this.defaultBlockState()
                .setValue(WATERLOGGED, levelAccessor.getFluidState(blockPos).getType() == Fluids.WATER)
                .setValue(FACING, context.getClickedFace());
    }

    @Override
    protected @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, POWERED, LIT);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    static {
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        POWERED = BlockStateProperties.POWERED;
        LIT = BlockStateProperties.LIT;

        double margin = 4;
        double height = 2;
        //noinspection SuspiciousNameCombination
        AABBS = Maps.newEnumMap(ImmutableMap.of(
                Direction.UP, Block.box(margin, 0, margin, 16 - margin, height, 16 - margin),
                Direction.DOWN, Block.box(margin, 16 - height, margin, 16 - margin, 16, 16 - margin),
                Direction.NORTH, Block.box(margin, margin, 16 - height, 16 - margin, 16 - margin, 16),
                Direction.SOUTH, Block.box(margin, margin, 0, 16 - margin, 16 - margin, height),
                Direction.EAST, Block.box(0, margin, margin, height, 16 - margin, 16 - margin),
                Direction.WEST, Block.box(16 - height, margin, margin, 16, 16 - margin, 16 - margin)
        ));
    }
}
