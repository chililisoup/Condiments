package dev.chililisoup.condiments.reg;

import dev.chililisoup.condiments.Condiments;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags {
    public static final TagKey<Item> CRATES = create("crates");

    private static TagKey<Item> create(String name) {
        return TagKey.create(Registries.ITEM, new ResourceLocation(Condiments.MOD_ID, name));
    }
}
