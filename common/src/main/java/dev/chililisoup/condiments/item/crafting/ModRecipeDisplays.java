package dev.chililisoup.condiments.item.crafting;

import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.block.CrateBlock;
import dev.chililisoup.condiments.item.component.CrateContents;
import dev.chililisoup.condiments.reg.ModBlocks;
import dev.chililisoup.condiments.reg.ModComponents;
import dev.chililisoup.condiments.reg.ModItemTags;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ModRecipeDisplays {
    public static List<RecipeHolder<CraftingRecipe>> getAll() {
        ArrayList<RecipeHolder<CraftingRecipe>> combined = new ArrayList<>();

        combined.addAll(crateColoringRecipe());
        combined.addAll(crateLockingRecipe());
        combined.addAll(crateUnlockingRecipe());

        return combined;
    }

    public static List<RecipeHolder<CraftingRecipe>> crateColoringRecipe() {
        ArrayList<RecipeHolder<CraftingRecipe>> recipeList = new ArrayList<>();
        String group = "crate_coloring";
        Ingredient ingredients = Ingredient.of(ModItemTags.CRATES);

        for (DyeColor color : DyeColor.values()) {
            DyeItem dye = DyeItem.byColor(color);
            ItemStack output = CrateBlock.getColoredItemStack(color);

            NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, ingredients, Ingredient.of(dye));

            ResourceLocation loc = Condiments.loc("/crate_coloring_" + color.getName());
            recipeList.add(new RecipeHolder<>(loc, new ShapelessRecipe(group, CraftingBookCategory.MISC, output, inputs)));
        }
        return recipeList;
    }

    public static List<RecipeHolder<CraftingRecipe>> crateLockingRecipe() {
        ArrayList<RecipeHolder<CraftingRecipe>> recipeList = new ArrayList<>();
        String group = "crate_locking";
        Ingredient ingredients = Ingredient.of(ModItemTags.CRATES);

        for (ItemStack input : ingredients.getItems()) {
            ItemStack output = input.copy();
            output.set(ModComponents.CRATE_CONTENTS.get(), new CrateContents(Optional.empty(), 0, Optional.of(true)));

            NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, Ingredient.of(input), Ingredient.of(Items.REDSTONE_TORCH));

            ResourceLocation loc = Condiments.loc("/crate_coloring_" + "crate_locking_" + input.getDescriptionId());
            recipeList.add(new RecipeHolder<>(loc, new ShapelessRecipe(group, CraftingBookCategory.MISC, output, inputs)));
        }

        return recipeList;
    }

    public static List<RecipeHolder<CraftingRecipe>> crateUnlockingRecipe() {
        ArrayList<RecipeHolder<CraftingRecipe>> recipeList = new ArrayList<>();
        String group = "crate_locking";
        Ingredient ingredients = Ingredient.of(ModItemTags.CRATES);

        for (ItemStack output : ingredients.getItems()) {
            ItemStack input = output.copy();
            input.set(ModComponents.CRATE_CONTENTS.get(), new CrateContents(Optional.empty(), 0, Optional.of(true)));

            NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, Ingredient.of(input), Ingredient.of(Items.STICK));

            ResourceLocation loc = Condiments.loc("/crate_unlocking_" + input.getDescriptionId());
            recipeList.add(new RecipeHolder<>(loc, new ShapelessRecipe(group, CraftingBookCategory.MISC, output, inputs)));
        }

        return recipeList;
    }

    public static Map<ItemLike, Component> ingredientInfos() {
        return Map.of(
                ModBlocks.BLACKENED_IRON_BLOCK.get(), Component.translatable("condiments.ingredient_info.blackened_iron_block")
        );
    }
}
