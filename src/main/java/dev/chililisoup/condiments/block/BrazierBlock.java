package dev.chililisoup.condiments.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//? if < 1.21 {
/*import net.minecraft.world.InteractionResult;
*///?} else {
import com.mojang.serialization.MapCodec;
import net.minecraft.world.ItemInteractionResult;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
//?}

public class BrazierBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    protected static final VoxelShape HIT_SHAPE = Block.box(4.5, 0.0, 4.5, 11.5, 9.0, 11.5);
    protected static final VoxelShape COLLISION_SHAPE = Block.box(4.5, 0.0, 4.5, 11.5, 7.0, 11.5);
    private final int fireDamage;

    //? if >= 1.21 {
    public static final MapCodec<BrazierBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.intRange(0, 1000).fieldOf("fire_damage").forGetter(brazier -> brazier.fireDamage),
                    propertiesCodec()
            ).apply(instance, BrazierBlock::new)
    );

    @Override
    protected @NotNull MapCodec<BrazierBlock> codec() {
        return CODEC;
    }
    //?}

    public BrazierBlock(int fireDamage, Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(WATERLOGGED, false)
                .setValue(LIT, false)
        );
        this.fireDamage = fireDamage;
    }

    @Override
    //$ public_now_protected
    protected
    void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (
                state.getValue(LIT) &&
                entity instanceof LivingEntity &&
                entity.getBoundingBox().intersects(HIT_SHAPE.bounds().move(pos))
        ) {
            entity.hurt(
                    //? if < 1.21 {
                    /*level.damageSources().inFire(),
                    *///?} else
                    level.damageSources().campfire(),
                    this.fireDamage
            );
        }

        super.entityInside(state, level, pos, entity);
    }

    @Override
    //? if < 1.21 {
    /*public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
    *///?} else
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        boolean toLight;
        boolean fireCharge = false;
        if (stack.getItem() instanceof FlintAndSteelItem) toLight = true;
        else if (stack.getItem() instanceof FireChargeItem) {
            toLight = true;
            fireCharge = true;
        } else if (stack.getItem() instanceof ShovelItem) toLight = false;
        //? if < 1.21 {
        /*else return InteractionResult.PASS;
        *///?} else
        else return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (state.getValue(LIT) == toLight || (toLight && state.getValue(WATERLOGGED))) {
            //? if < 1.21 {
            /*return InteractionResult.PASS;
            *///?} else
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (toLight) {
            level.playSound(
                    player,
                    pos,
                    fireCharge ? SoundEvents.FIRECHARGE_USE : SoundEvents.FLINTANDSTEEL_USE,
                    SoundSource.BLOCKS,
                    1.0F,
                    level.getRandom().nextFloat() * 0.4F + 0.8F
            );
        } else {
            if (!level.isClientSide) level.levelEvent(null, 1009, pos, 0);
            else makeDowseParticles(level, pos);
        }

        level.setBlock(pos, state.setValue(LIT, toLight), 11);
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);

        if (fireCharge) {
            //? if < 1.21 {
            /*if (!player.getAbilities().instabuild) stack.shrink(1);
            *///?} else
            stack.consume(1, player);
        } else stack.hurtAndBreak(
                1,
                player,
                //? if < 1.21 {
                /*eventPlayer -> eventPlayer.broadcastBreakEvent(hand)
                *///?} else
                LivingEntity.getSlotForHand(hand)
        );

        //? if < 1.21 {
        /*return InteractionResult.sidedSuccess(level.isClientSide);
        *///?} else
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (state.getValue(BlockStateProperties.WATERLOGGED) || fluidState.getType() != Fluids.WATER)
            return false;

        if (state.getValue(LIT)) {
            if (!level.isClientSide())
                level.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
            else makeDowseParticles(level, pos);
        }

        level.setBlock(pos, state.setValue(WATERLOGGED, true).setValue(LIT, false), 3);
        level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
        level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));

        return true;
    }

    public static void makeDowseParticles(LevelAccessor level, BlockPos pos) {
        for (int i = 0; i < 20; i++) makeParticles(level, pos, level.getRandom());
    }

    public static void makeParticles(LevelAccessor level, BlockPos pos, RandomSource randomSource) {
        level.addParticle(
                ParticleTypes.SMOKE,
                pos.getX() + 0.5 + randomSource.nextDouble() / 6.0 * (randomSource.nextBoolean() ? 1 : -1),
                pos.getY() + 0.5,
                pos.getZ() + 0.5 + randomSource.nextDouble() / 6.0 * (randomSource.nextBoolean() ? 1 : -1),
                0.0,
                0.005,
                0.0
        );
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT)) return;

        if (random.nextInt(24) == 0) level.playLocalSound(
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                SoundEvents.FIRE_AMBIENT,
                SoundSource.BLOCKS,
                1.0F + random.nextFloat(),
                random.nextFloat() * 0.7F + 0.3F,
                false
        );

        makeParticles(level, pos, random);
    }

    @Override
    //$ public_now_protected
    protected
    @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return HIT_SHAPE;
    }

    @Override
    //$ public_now_protected
    protected
    @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return COLLISION_SHAPE;
    }

    @Override
    //$ public_now_protected
    protected
    @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
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
                .setValue(WATERLOGGED, levelAccessor.getFluidState(blockPos).getType() == Fluids.WATER);
    }

    @Override
    //$ public_now_protected
    protected
     @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, LIT);
    }

    @Override
    //$ public_now_protected
    protected
    boolean isPathfindable(
            BlockState state,
            //? if < 1.21
            /*BlockGetter level, BlockPos pos,*/
            PathComputationType pathComputationType
    ) {
        return false;
    }
}
