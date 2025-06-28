package dev.chililisoup.condiments.compat;

import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.item.crafting.ModRecipeDisplays;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.forge.REIPluginClient;
import me.shedaniel.rei.plugin.common.displays.DefaultInformationDisplay;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.network.chat.Component;

@REIPluginClient
public class ReiCompat implements REIClientPlugin {
    @Override
    public void registerDisplays(DisplayRegistry registry) {
        ModRecipeDisplays.getAll().forEach(recipe -> registry.add(DefaultCraftingDisplay.of(recipe)));

        ModRecipeDisplays.ingredientInfos().forEach((ingredient, info) ->
                registry.add(DefaultInformationDisplay.createFromEntry(
                        EntryStack.of(VanillaEntryTypes.ITEM, ingredient.asItem().getDefaultInstance()),
                        Component.literal(Condiments.loc("/info_" + Utils.getID(ingredient.asItem()).getPath()).toString())
                ).line(info))
        );
    }
}
