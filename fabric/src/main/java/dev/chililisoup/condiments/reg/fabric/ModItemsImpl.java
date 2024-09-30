package dev.chililisoup.condiments.reg.fabric;

import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.fabric.CondimentsFabric;
import dev.chililisoup.condiments.reg.ModItems;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

import java.util.function.Supplier;

public class ModItemsImpl {
    public static Supplier<Item> addItem(ModItems.Params params) {
        ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(Condiments.MOD_ID, params.id);
        Item item = params.itemFactory.get();
        Registry.register(BuiltInRegistries.ITEM, loc, item);

        for (String tab : params.creativeTabs) {
            ResourceKey<CreativeModeTab> itemGroup = CondimentsFabric.getCreativeTab(tab);
            ItemGroupEvents.modifyEntriesEvent(itemGroup).register(content -> content.addAfter(Items.AIR, item));
        }

        return () -> item;
    }
}
