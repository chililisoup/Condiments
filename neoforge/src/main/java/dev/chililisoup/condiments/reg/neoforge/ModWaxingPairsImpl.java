package dev.chililisoup.condiments.reg.neoforge;

import dev.chililisoup.condiments.Condiments;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class ModWaxingPairsImpl {
    //private static final BiMap<Block, Block> waxingPairs = HashBiMap.create();

    public static void addWaxingPair(Supplier<Block> unwaxed, Supplier<Block> waxed) {
        //waxingPairs.put(unwaxed.get(), waxed.get());
        Condiments.LOGGER.info("Waxing currently not supported on Forge...");
    }
}
