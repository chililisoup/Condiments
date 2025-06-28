package dev.chililisoup.condiments.reg;

import dev.chililisoup.condiments.Condiments;
import net.mehvahdjukaar.moonlight.api.misc.RegSupplier;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.world.item.Item;
import java.util.function.Supplier;

public class ModItems {
    public static Supplier<Item> BLACKENED_IRON_INGOT;

    public static Supplier<Item> addItem(Params params) {
        RegSupplier<? extends Item> regSupplier = RegHelper.registerItem(
                Condiments.loc(params.id),
                params.itemFactory
        );

        return regSupplier::get;
    }

    public static void init() {
        BLACKENED_IRON_INGOT = addItem(new Params("blackened_iron_ingot", () -> new Item(new Item.Properties())));
    }

    public record Params(String id, Supplier<? extends Item> itemFactory) {}
}
