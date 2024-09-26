package dev.chililisoup.condiments.reg.neoforge;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.function.Supplier;

public class ModColorProvidersImpl {
    public static final ArrayList<Pair<BlockColor, Supplier<Block>>> BLOCK_COLORS = new ArrayList<>();

    public static void addColorProvider(BlockColor color, Supplier<Block> block) {
        BLOCK_COLORS.add(new Pair<>(color, block));
    }
}
