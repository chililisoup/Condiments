package dev.chililisoup.condiments.block;

import dev.chililisoup.condiments.reg.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

//? if >= 1.21
import com.mojang.serialization.MapCodec;

public class CopperFireBlock extends BaseFireBlock {
    //? if >= 1.21 {
    public static final MapCodec<CopperFireBlock> CODEC = simpleCodec(CopperFireBlock::new);

    @Override
    protected @NotNull MapCodec<CopperFireBlock> codec() {
        return CODEC;
    }
    //?}

    public CopperFireBlock(BlockBehaviour.Properties properties) {
        super(properties, 1.0F);
    }

    @Override
    //$ public_now_protected
    protected
    @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return this.canSurvive(state, level, pos) ? this.defaultBlockState() : Blocks.AIR.defaultBlockState();
    }

    @Override
    //$ public_now_protected
    protected
    boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return canSurviveOnBlock(level.getBlockState(pos.below()), level, pos.below());
    }

    public static boolean canSurviveOnBlock(BlockState state, BlockGetter level, BlockPos pos) {
        if (state.is(ModBlockTags.COPPER_FIRE_BASE_BLOCKS) || state.getBlock() instanceof WeatheringCopper)
            return state.isFaceSturdy(level, pos, Direction.UP);
        else return false;
    }

    @Override
    protected boolean canBurn(BlockState state) {
        return true;
    }
}
