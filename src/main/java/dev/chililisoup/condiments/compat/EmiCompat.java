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
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;

//? if >= 1.21
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

@EmiEntrypoint
public class EmiCompat implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        ModRecipeDisplays.getAll().forEach(recipeHolder -> registry.addRecipe(emiRecipe(recipeHolder)));

        ModRecipeDisplays.ingredientInfos().forEach((ingredient, info) ->
                registry.addRecipe(new EmiInfoRecipe(
                        List.of(EmiStack.of(ingredient)),
                        List.of(info),
                        Condiments.loc("/info_" + Utils.getID(ingredient.asItem()).getPath())
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
