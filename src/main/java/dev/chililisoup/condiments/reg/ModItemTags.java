package dev.chililisoup.condiments.reg;

import dev.chililisoup.condiments.Condiments;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModItemTags {
    public static final TagKey<Item> CRATES = create("crates");
    public static final TagKey<Item> CRATE_LOCKING_ITEMS = create("crate_locking_items");
    public static final TagKey<Item> CRATE_UNLOCKING_ITEMS = create("crate_unlocking_items");

    private static TagKey<Item> create(String name) {
        return TagKey.create(Registries.ITEM, Condiments.loc(name));
    }
}
