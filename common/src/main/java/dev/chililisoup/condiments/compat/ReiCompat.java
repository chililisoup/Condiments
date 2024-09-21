package dev.chililisoup.condiments.compat;

import dev.chililisoup.condiments.item.crafting.ModRecipeDisplays;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;

public class ReiCompat implements REIClientPlugin {
    @Override
    public void registerDisplays(DisplayRegistry registry) {
        ModRecipeDisplays.crateColoringRecipe().forEach(recipe -> registry.add((DefaultCraftingDisplay.of(recipe))));
        ModRecipeDisplays.crateLockingRecipe().forEach(recipe -> registry.add((DefaultCraftingDisplay.of(recipe))));
        ModRecipeDisplays.crateUnlockingRecipe().forEach(recipe -> registry.add((DefaultCraftingDisplay.of(recipe))));
    }
}
