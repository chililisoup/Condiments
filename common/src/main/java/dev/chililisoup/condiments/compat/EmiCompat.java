package dev.chililisoup.condiments.compat;

import dev.chililisoup.condiments.item.crafting.ModRecipeDisplays;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapelessRecipe;

@EmiEntrypoint
public class EmiCompat implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        ModRecipeDisplays.crateColoringRecipe().forEach(recipeHolder -> registry.addRecipe(emiRecipe(recipeHolder)));
        ModRecipeDisplays.crateLockingRecipe().forEach(recipeHolder -> registry.addRecipe(emiRecipe(recipeHolder)));
        ModRecipeDisplays.crateUnlockingRecipe().forEach(recipeHolder -> registry.addRecipe(emiRecipe(recipeHolder)));
    }

    private static EmiCraftingRecipe emiRecipe(RecipeHolder<CraftingRecipe> recipeHolder) {
        CraftingRecipe recipe = recipeHolder.value();

        return new EmiCraftingRecipe(
                recipe.getIngredients().stream().map(EmiIngredient::of).toList(),
                EmiStack.of(recipe.getResultItem(null)),
                recipeHolder.id(),
                recipe instanceof ShapelessRecipe
        );
    }
}
