package dev.chililisoup.condiments.reg;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class ModItems {
    @ExpectPlatform
    private static Supplier<Item> addItem(ModItems.Params params) {
        throw new AssertionError();
    }

    public static void init() {
        addItem(new Params("blackened_iron_ingot", () -> new Item(new Item.Properties())).creativeTabs("INGREDIENTS"));
    }

    public static class Params {
        public final String id;
        public final Supplier<? extends Item> itemFactory;
        public String[] creativeTabs = new String[]{};

        public Params(String id, Supplier<? extends Item> itemFactory) {
            this.id = id;
            this.itemFactory = itemFactory;
        }

        public ModItems.Params creativeTabs(String... tabs) {
            creativeTabs = tabs;
            return this;
        }
    }
}
