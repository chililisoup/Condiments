package dev.chililisoup.condiments.reg.neoforge;

import dev.chililisoup.condiments.neoforge.CondimentsNeoForge;
import dev.chililisoup.condiments.reg.ModItems;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class ModItemsImpl {
    public static Supplier<Item> addItem(ModItems.Params params) {
        return CondimentsNeoForge.registerItem(params);
    }
}
