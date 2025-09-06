package dev.chililisoup.condiments.reg;

import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.extra.VersionHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModBlockTags {
    public static final TagKey<Block> CRATES = create("crates");
    public static final TagKey<Block> WOOD_WALLS = create("wood_walls");
    public static final TagKey<Block> COPPER_FIRE_BASE_BLOCKS = create("copper_fire_base_blocks");

    public static final TagKey<Block> TINTED_GLASS = createCommon("glass_blocks/tinted");

    private static TagKey<Block> create(String name) {
        return TagKey.create(Registries.BLOCK, Condiments.loc(name));
    }

    private static TagKey<Block> createCommon(String name) {
        return TagKey.create(Registries.BLOCK, VersionHelper.resourceLocation("c", name));
    }
}
