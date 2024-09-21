package dev.chililisoup.condiments.reg.forge;

import dev.chililisoup.condiments.forge.CondimentsForge;
import dev.chililisoup.condiments.reg.ModBlocks;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class ModBlocksImpl {
    public static Supplier<Block> addBlock(ModBlocks.Params params) {
        return CondimentsForge.registerBlock(params);
    }
}
