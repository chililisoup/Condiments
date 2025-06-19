package dev.chililisoup.condiments.compat.neoforge;

import dev.chililisoup.condiments.neoforge.CondimentsNeoForge;
import net.minecraft.world.level.block.Block;

public class MoonlightCompatImpl {
    public static void markFlammable(Block block) {
        CondimentsNeoForge.FLAMMABLE_BLOCKS.add(() -> block);
    }
}
