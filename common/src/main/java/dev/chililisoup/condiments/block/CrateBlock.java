package dev.chililisoup.condiments.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.chililisoup.condiments.block.entity.CrateBlockEntity;
import dev.chililisoup.condiments.reg.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class CrateBlock extends BaseEntityBlock {
    public static final MapCodec<CrateBlock> CODEC = RecordCodecBuilder.mapCodec(
            (instance) -> instance.group(DyeColor.CODEC.optionalFieldOf("color").forGetter(
                    (crateBlock) -> Optional.ofNullable(crateBlock.color)), propertiesCodec()).apply(instance,
                    (optional, properties) -> new CrateBlock(optional.orElse(null), properties)));
    private static final EnumProperty<FrontAndTop> ORIENTATION;
    @Nullable private final DyeColor color;
    
    public CrateBlock(@Nullable DyeColor color, BlockBehaviour.Properties properties) {
        super(properties);
        this.color = color;
        this.registerDefaultState(this.stateDefinition.any().setValue(ORIENTATION, FrontAndTop.NORTH_UP));
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
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        boolean hitFace = hitResult.getDirection() == state.getValue(ORIENTATION).front();

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof CrateBlockEntity && hitFace)) return InteractionResult.PASS;

        Optional<Vec2> hitPos = getHitPosition(hitResult, state.getValue(ORIENTATION).front());
        if (hitPos.isEmpty()) return InteractionResult.PASS;
        if (isNotInBounds(hitPos.get())) return InteractionResult.PASS;

        if (!level.isClientSide)
            ((CrateBlockEntity) blockEntity).tryAddStack(ItemStack.EMPTY, player);

        return InteractionResult.SUCCESS;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        boolean hitFace = hitResult.getDirection() == state.getValue(ORIENTATION).front();

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof CrateBlockEntity && hitFace)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        Optional<Vec2> hitPos = getHitPosition(hitResult, state.getValue(ORIENTATION).front());
        if (hitPos.isEmpty()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (isNotInBounds(hitPos.get())) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (!level.isClientSide)
            player.setItemInHand(hand, ((CrateBlockEntity) blockEntity).tryAddStack(player.getItemInHand(hand), player));

        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (level.isClientSide) return;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof CrateBlockEntity)) return;

        BlockHitResult hitResult = getHitResult(level, pos, player);
        if (hitResult.getDirection() != state.getValue(ORIENTATION).front()) return;

        Optional<Vec2> hitPos = getHitPosition(hitResult, state.getValue(ORIENTATION).front());

        if (hitPos.isEmpty()) return;
        if (isNotInBounds(hitPos.get())) return;

        ItemStack itemStack = ((CrateBlockEntity) blockEntity).request(player.isShiftKeyDown());
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
    public @NotNull BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(level, pos, state, player);

        if (!level.isClientSide && player.isCreative() && !((CrateBlockEntity) Objects.requireNonNull(level.getBlockEntity(pos))).isEmpty()) {
            Block.getDrops(state, (ServerLevel) level, pos, level.getBlockEntity(pos)).forEach(drop -> {
                ItemEntity ent = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
                ent.setDefaultPickUpDelay();
                ent.setDeltaMovement(0, 0, 0);
                level.addFreshEntity(ent);
            });
        }

        return state;
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

    @Nullable
    public static DyeColor getColorFromItem(Item item) {
        return getColorFromBlock(Block.byItem(item));
    }

    @Nullable
    public static DyeColor getColorFromBlock(Block block) {
        return block instanceof CrateBlock ? ((CrateBlock)block).getColor() : null;
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

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrateBlockEntity(pos, state);
    }


    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
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
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction front = context.getNearestLookingDirection().getOpposite();
        Direction top = switch (front) {
            case DOWN -> context.getHorizontalDirection().getOpposite();
            case UP -> context.getHorizontalDirection();
            case NORTH, SOUTH, WEST, EAST -> Direction.UP;
        };

        return this.defaultBlockState().setValue(ORIENTATION, FrontAndTop.fromFrontAndTop(front, top));
    }

    @Override
    protected @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(ORIENTATION, rotation.rotation().rotate(state.getValue(ORIENTATION)));
    }

    @Override
    protected @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(ORIENTATION, mirror.rotation().rotate(state.getValue(ORIENTATION)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ORIENTATION);
    }

    static {
        ORIENTATION = BlockStateProperties.ORIENTATION;
    }
}
