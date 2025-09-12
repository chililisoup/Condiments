package dev.chililisoup.condiments.compat;

import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.item.crafting.ModRecipeDisplays;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.plugin.common.displays.DefaultInformationDisplay;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import net.minecraft.network.chat.Component;

//? if forgeLike
/*@me.shedaniel.rei.forge.REIPluginClient*/
public class ReiCompat implements REIClientPlugin {
    @Override
    public void registerDisplays(DisplayRegistry registry) {
        ModRecipeDisplays.getAll().forEach(recipe -> registry.add(DefaultCraftingDisplay.of(recipe)));

        ModRecipeDisplays.ingredientInfos().forEach((ingredients, infos) ->
                registry.add(DefaultInformationDisplay.createFromEntries(
                        EntryIngredient.of(
                                ingredients.stream().map(
                                        ingredient -> EntryStack.of(VanillaEntryTypes.ITEM, ingredient.asItem().getDefaultInstance())
                                ).toList()
                        ),
                        Component.literal(Condiments.loc("/info_" + infos.get(0)).toString())
                ).lines(infos.stream().map(info -> (Component) Component.translatable(info)).toList()))
        );
    }
}
