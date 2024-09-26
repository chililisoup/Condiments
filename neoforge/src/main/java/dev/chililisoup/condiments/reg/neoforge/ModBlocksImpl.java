package dev.chililisoup.condiments.reg.neoforge;

import dev.chililisoup.condiments.neoforge.CondimentsNeoForge;
import dev.chililisoup.condiments.reg.ModBlocks;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class ModBlocksImpl {
    public static Supplier<Block> addBlock(ModBlocks.Params params) {
        return CondimentsNeoForge.registerBlock(params);
    }
}
