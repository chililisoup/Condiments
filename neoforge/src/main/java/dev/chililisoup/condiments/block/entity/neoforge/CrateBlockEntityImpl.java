package dev.chililisoup.condiments.block.entity.neoforge;

import dev.chililisoup.condiments.block.entity.CrateBlockEntity;
import dev.chililisoup.condiments.neoforge.item.CrateItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CrateBlockEntityImpl extends CrateBlockEntity {
    public final CrateItemHandler handler;

    protected CrateBlockEntityImpl(BlockPos pos, BlockState blockState) {
        super(pos, blockState);
        this.handler = new CrateItemHandler(this);
    }

    public static CrateBlockEntity of(BlockPos pos, BlockState blockState) {
        return new CrateBlockEntityImpl(pos, blockState);
    }
}
