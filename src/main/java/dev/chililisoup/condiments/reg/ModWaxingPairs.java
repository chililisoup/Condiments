package dev.chililisoup.condiments.reg;

import dev.chililisoup.condiments.config.CommonConfig;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Supplier;

public class ModWaxingPairs {
    public static void init() {
        if (CommonConfig.BLACKENED_IRON.get()) addWaxingPair(() -> Blocks.IRON_BLOCK, ModBlocks.WAXED_IRON_BLOCK);
    }

    public static void addWaxingPair(Supplier<Block> unwaxed, Supplier<Block> waxed) {
        //? if fabric
        net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry.registerWaxableBlockPair(unwaxed.get(), waxed.get());
    }
}
