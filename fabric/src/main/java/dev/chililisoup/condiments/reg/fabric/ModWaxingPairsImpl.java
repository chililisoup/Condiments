package dev.chililisoup.condiments.reg.fabric;

import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class ModWaxingPairsImpl {
    public static void addWaxingPair(Supplier<Block> unwaxed, Supplier<Block> waxed) {
        OxidizableBlocksRegistry.registerWaxableBlockPair(unwaxed.get(), waxed.get());
    }
}
