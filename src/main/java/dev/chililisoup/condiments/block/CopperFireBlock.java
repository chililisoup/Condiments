package dev.chililisoup.condiments.block;

import com.mojang.serialization.MapCodec;
import dev.chililisoup.condiments.reg.ModBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CopperFireBlock extends BaseFireBlock {
    public static final MapCodec<CopperFireBlock> CODEC = simpleCodec(CopperFireBlock::new);

    @Override
    public @NotNull MapCodec<CopperFireBlock> codec() {
        return CODEC;
    }

    public CopperFireBlock(BlockBehaviour.Properties properties) {
        super(properties, 1.0F);
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return this.canSurvive(state, level, pos) ? this.defaultBlockState() : Blocks.AIR.defaultBlockState();
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return canSurviveOnBlock(level.getBlockState(pos.below()));
    }

    public static boolean canSurviveOnBlock(BlockState state) {
        return state.is(ModBlockTags.COPPER_FIRE_BASE_BLOCKS) || state.getBlock() instanceof WeatheringCopper;
    }

    @Override
    protected boolean canBurn(BlockState state) {
        return true;
    }
}
