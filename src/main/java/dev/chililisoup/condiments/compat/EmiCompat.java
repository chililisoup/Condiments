package dev.chililisoup.condiments.compat;

import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.item.crafting.ModRecipeDisplays;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;

//? if >= 1.21
import net.minecraft.world.item.crafting.RecipeHolder;

@EmiEntrypoint
public class EmiCompat implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        ModRecipeDisplays.getAll().forEach(recipeHolder -> registry.addRecipe(emiRecipe(recipeHolder)));

        ModRecipeDisplays.ingredientInfos().forEach((ingredients, infos) ->
                registry.addRecipe(new EmiInfoRecipe(
                        ingredients.stream().map(ingredient -> (EmiIngredient) EmiStack.of(ingredient)).toList(),
                        infos.stream().map(info -> (Component) Component.translatable(info)).toList(),
                        Condiments.loc("/info_" + infos.get(0))
                ))
        );
    }

    //? if < 1.21 {
    /*private static EmiCraftingRecipe emiRecipe(CraftingRecipe recipe) {
        return new EmiCraftingRecipe(
                recipe.getIngredients().stream().map(EmiIngredient::of).toList(),
                EmiStack.of(recipe.getResultItem(null)),
                recipe.getId(),
                recipe instanceof ShapelessRecipe
        );
    }
    *///?} else {
    private static EmiCraftingRecipe emiRecipe(RecipeHolder<CraftingRecipe> recipeHolder) {
        CraftingRecipe recipe = recipeHolder.value();

        return new EmiCraftingRecipe(
                recipe.getIngredients().stream().map(EmiIngredient::of).toList(),
                EmiStack.of(recipe.getResultItem(null)),
                recipeHolder.id(),
                recipe instanceof ShapelessRecipe
        );
    }
    //?}
}
