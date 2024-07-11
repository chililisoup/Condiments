package dev.chililisoup.condiments.block;

import dev.chililisoup.condiments.block.entity.CrateBlockEntity;
import dev.chililisoup.condiments.reg.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CrateBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING;
    @Nullable
    private final DyeColor color;
    
    public CrateBlock(@Nullable DyeColor color, BlockBehaviour.Properties properties) {
        super(properties);
        this.color = color;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }


    public static BlockHitResult getHitResult(Level level, BlockPos pos, Entity player) {
        Vec3 eyePos = player.getEyePosition(1);
        return level.clip(new ClipContext(
                eyePos,
                eyePos.add(player.getViewVector(1).scale(eyePos.distanceTo(Vec3.atCenterOf(pos)) + 1)),
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                player
        ));
    }

    public static Optional<Vec2> getHitPosition(BlockHitResult hitResult, Direction face) {
        Direction direction = hitResult.getDirection();
        if (face != direction) return Optional.empty();

        BlockPos blockPos = hitResult.getBlockPos().relative(direction);
        Vec3 vec3 = hitResult.getLocation().subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        double x = vec3.x();
        double y = vec3.y();
        double z = vec3.z();

        return switch (direction) {
            case NORTH, SOUTH -> Optional.of(new Vec2((float) x, (float) y));
            case WEST, EAST -> Optional.of(new Vec2((float) z, (float) y));
            case UP, DOWN -> Optional.of(new Vec2((float) x, (float) z));
        };
    }

    public static boolean isNotInBounds(Vec2 pos) {
        return pos.x < 0.125 || pos.x > 0.875 ||
               pos.y < 0.125 || pos.y > 0.875;
    }

    @Override
    public @NotNull InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        boolean hitFace = hitResult.getDirection() == state.getValue(FACING);
        if (level.isClientSide) return hitFace ? InteractionResult.SUCCESS : InteractionResult.PASS;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof CrateBlockEntity && hitFace)) return InteractionResult.PASS;

        Optional<Vec2> hitPos = getHitPosition(hitResult, state.getValue(FACING));
        if (hitPos.isEmpty()) return InteractionResult.PASS;
        if (isNotInBounds(hitPos.get())) return InteractionResult.PASS;

        player.setItemInHand(hand, ((CrateBlockEntity) blockEntity).tryAddStack(player.getItemInHand(hand), player));

        return InteractionResult.SUCCESS;
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (level.isClientSide) return;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof CrateBlockEntity)) return;

        BlockHitResult hitResult = getHitResult(level, pos, player);
        if (hitResult.getDirection() != state.getValue(FACING)) return;

        Optional<Vec2> hitPos = getHitPosition(hitResult, state.getValue(FACING));

        if (hitPos.isEmpty()) return;
        if (isNotInBounds(hitPos.get())) return;

        ItemStack itemStack = ((CrateBlockEntity) blockEntity).request(player.isCrouching());
        player.addItem(itemStack);

        if (itemStack.getCount() > 0) {
            ItemEntity itemEntity = player.drop(itemStack, false);
            if (itemEntity != null) {
                itemEntity.setNoPickUpDelay();
                itemEntity.setTarget(player.getUUID());
            }
        }
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(level, pos, state, player);

        if (!level.isClientSide && player.isCreative() && !((CrateBlockEntity) Objects.requireNonNull(level.getBlockEntity(pos))).isEmpty()) {
            Block.getDrops(state, (ServerLevel) level, pos, level.getBlockEntity(pos)).forEach(drop -> {
                ItemEntity ent = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
                ent.setDefaultPickUpDelay();
                ent.setDeltaMovement(0, 0, 0);
                level.addFreshEntity(ent);
            });
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (stack.hasCustomHoverName()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof CrateBlockEntity) {
                ((CrateBlockEntity) blockEntity).setCustomName(stack.getHoverName());
            }
        }

    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof CrateBlockEntity) {
                level.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    public static Block getBlockByColor(@Nullable DyeColor color) {
        if (color == null) return ModBlocks.CRATE.get();
        else return switch (color) {
            case WHITE -> ModBlocks.WHITE_CRATE.get();
            case LIGHT_GRAY -> ModBlocks.LIGHT_GRAY_CRATE.get();
            case GRAY -> ModBlocks.GRAY_CRATE.get();
            case BLACK -> ModBlocks.BLACK_CRATE.get();
            case BROWN -> ModBlocks.BROWN_CRATE.get();
            case RED -> ModBlocks.RED_CRATE.get();
            case ORANGE -> ModBlocks.ORANGE_CRATE.get();
            case YELLOW -> ModBlocks.YELLOW_CRATE.get();
            case LIME -> ModBlocks.LIME_CRATE.get();
            case GREEN -> ModBlocks.GREEN_CRATE.get();
            case CYAN -> ModBlocks.CYAN_CRATE.get();
            case LIGHT_BLUE -> ModBlocks.LIGHT_BLUE_CRATE.get();
            case BLUE -> ModBlocks.BLUE_CRATE.get();
            case PURPLE -> ModBlocks.PURPLE_CRATE.get();
            case MAGENTA -> ModBlocks.MAGENTA_CRATE.get();
            default -> ModBlocks.PINK_CRATE.get();
        };
    }

    @Nullable
    public DyeColor getColor() {
        return this.color;
    }

    public static ItemStack getColoredItemStack(@Nullable DyeColor color) {
        return new ItemStack(getBlockByColor(color));
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrateBlockEntity(pos, state);
    }


    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
    }

    public static @NotNull List<ItemStack> getDrops(BlockState state, ServerLevel level, BlockPos pos, @Nullable BlockEntity blockEntity) {
        LootParams.Builder builder = (new LootParams.Builder(level)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity);
        return state.getDrops(builder);
    }

    public static @NotNull List<ItemStack> getDrops(BlockState state, ServerLevel level, BlockPos pos, @Nullable BlockEntity blockEntity, @Nullable Entity entity, ItemStack tool) {
        LootParams.Builder builder = (new LootParams.Builder(level)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL, tool).withOptionalParameter(LootContextParams.THIS_ENTITY, entity).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity);
        return state.getDrops(builder);
    }

    static {
        FACING = BlockStateProperties.FACING;
    }
}
