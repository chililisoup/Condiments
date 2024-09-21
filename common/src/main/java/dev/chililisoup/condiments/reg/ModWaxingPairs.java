package dev.chililisoup.condiments.reg;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Supplier;

public class ModWaxingPairs {
    public static void init() {
        addWaxingPair(() -> Blocks.IRON_BLOCK, ModBlocks.WAXED_IRON_BLOCK);
    }

    @ExpectPlatform
    public static void addWaxingPair(Supplier<Block> unwaxed, Supplier<Block> waxed) {
        throw new AssertionError();
    }
}
