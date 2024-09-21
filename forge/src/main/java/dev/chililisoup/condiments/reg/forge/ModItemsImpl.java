package dev.chililisoup.condiments.reg.forge;

import dev.chililisoup.condiments.forge.CondimentsForge;
import dev.chililisoup.condiments.reg.ModItems;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class ModItemsImpl {
    public static Supplier<Item> addItem(ModItems.Params params) {
        return CondimentsForge.registerItem(params);
    }
}
