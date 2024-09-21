package dev.chililisoup.condiments.block;

import dev.chililisoup.condiments.reg.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class WaxedIronBlock extends Block {
    public WaxedIronBlock(Properties properties) {
        super(properties);
    }

    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getBlockState(pos.above()).is(Blocks.FIRE) || level.getBlockState(pos.below()).is(Blocks.FIRE)) {
            level.setBlockAndUpdate(pos, ModBlocks.BLACKENED_IRON_BLOCK.get().defaultBlockState());
        }
    }
}
