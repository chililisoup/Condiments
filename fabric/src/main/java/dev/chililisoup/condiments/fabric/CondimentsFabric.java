package dev.chililisoup.condiments.fabric;

import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.reg.ModBlockEntities;
import dev.chililisoup.condiments.reg.ModDispenserBehaviors;
//import dev.chililisoup.condiments.reg.ModWaxingPairs;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;

public class CondimentsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Condiments.init();
        ModBlockEntities.init();
        ModDispenserBehaviors.init();
        //ModWaxingPairs.init();
    }

    public static ResourceKey<CreativeModeTab> getCreativeTab(String tab) {
        return switch (tab) {
            case "BUILDING_BLOCKS" -> CreativeModeTabs.BUILDING_BLOCKS;
            case "COLORED_BLOCKS" -> CreativeModeTabs.COLORED_BLOCKS;
            case "NATURAL_BLOCKS" -> CreativeModeTabs.NATURAL_BLOCKS;
            case "FUNCTIONAL_BLOCKS" -> CreativeModeTabs.FUNCTIONAL_BLOCKS;
            case "REDSTONE_BLOCKS" -> CreativeModeTabs.REDSTONE_BLOCKS;
            case "TOOLS_AND_UTILITIES" -> CreativeModeTabs.TOOLS_AND_UTILITIES;
            case "COMBAT" -> CreativeModeTabs.COMBAT;
            case "FOOD_AND_DRINKS" -> CreativeModeTabs.FOOD_AND_DRINKS;
            case "INGREDIENTS" -> CreativeModeTabs.INGREDIENTS;
            case "SPAWN_EGGS" -> CreativeModeTabs.SPAWN_EGGS;
            default -> CreativeModeTabs.OP_BLOCKS;
        };
    }
}