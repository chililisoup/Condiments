package dev.chililisoup.condiments.reg.fabric;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class ModColorProvidersImpl {
    public static void addColorProvider(BlockColor color, Supplier<Block> block) {
        ColorProviderRegistry.BLOCK.register(color, block.get());
    }
}
