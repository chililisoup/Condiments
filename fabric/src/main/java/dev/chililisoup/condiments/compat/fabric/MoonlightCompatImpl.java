package dev.chililisoup.condiments.compat.fabric;

import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.world.level.block.Block;

public class MoonlightCompatImpl {
    public static void markFlammable(Block block) {
        FlammableBlockRegistry.getDefaultInstance().add(block, 5, 5);
    }
}
