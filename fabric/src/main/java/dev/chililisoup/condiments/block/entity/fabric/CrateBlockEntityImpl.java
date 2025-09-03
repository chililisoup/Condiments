package dev.chililisoup.condiments.block.entity.fabric;

import dev.chililisoup.condiments.block.entity.CrateBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CrateBlockEntityImpl extends CrateBlockEntity {
    protected CrateBlockEntityImpl(BlockPos pos, BlockState blockState) {
        super(pos, blockState);
    }

    public static CrateBlockEntity of(BlockPos pos, BlockState blockState) {
        return new CrateBlockEntityImpl(pos, blockState);
    }
}
