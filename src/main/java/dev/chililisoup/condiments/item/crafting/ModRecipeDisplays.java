package dev.chililisoup.condiments.item.crafting;

import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.block.CrateBlock;
import dev.chililisoup.condiments.block.entity.CrateContents;
import dev.chililisoup.condiments.reg.ModBlocks;
import dev.chililisoup.condiments.reg.ModItemTags;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModRecipeDisplays {
    //? if < 1.21 {
    /*public static List<CraftingRecipe> getAll() {
        ArrayList<CraftingRecipe> combined = new ArrayList<>();
    *///?} else {
    public static List<RecipeHolder<CraftingRecipe>> getAll() {
        ArrayList<RecipeHolder<CraftingRecipe>> combined = new ArrayList<>();
    //?}
        combined.addAll(crateColoringRecipe());
        combined.addAll(crateLockingRecipe());
        combined.addAll(crateUnlockingRecipe());

        return combined;
    }

    //? if < 1.21 {
    /*public static List<CraftingRecipe> crateColoringRecipe() {
        ArrayList<CraftingRecipe> recipeList = new ArrayList<>();
    *///?} else {
    public static List<RecipeHolder<CraftingRecipe>> crateColoringRecipe() {
        ArrayList<RecipeHolder<CraftingRecipe>> recipeList = new ArrayList<>();
    //?}
        String group = "crate_coloring";
        Ingredient ingredients = Ingredient.of(ModItemTags.CRATES);

        for (DyeColor color : DyeColor.values()) {
            DyeItem dye = DyeItem.byColor(color);
            ItemStack output = CrateBlock.getColoredItemStack(color);

            NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, ingredients, Ingredient.of(dye));

            ResourceLocation loc = Condiments.loc("/crate_coloring_" + color.getName());
            //? if < 1.21 {
            /*recipeList.add(new ShapelessRecipe(loc, group, CraftingBookCategory.MISC, output, inputs));
            *///?} else
            recipeList.add(new RecipeHolder<>(loc, new ShapelessRecipe(group, CraftingBookCategory.MISC, output, inputs)));
        }
        return recipeList;
    }

    //? if < 1.21 {
    /*public static List<CraftingRecipe> crateLockingRecipe() {
        ArrayList<CraftingRecipe> recipeList = new ArrayList<>();
    *///?} else {
    public static List<RecipeHolder<CraftingRecipe>> crateLockingRecipe() {
        ArrayList<RecipeHolder<CraftingRecipe>> recipeList = new ArrayList<>();
    //?}
        String group = "crate_locking";
        Ingredient ingredients = Ingredient.of(ModItemTags.CRATES);

        for (ItemStack input : ingredients.getItems()) {
            ItemStack output = input.copy();
            new CrateContents(null, 0, true).updateCrateItem(output);

            NonNullList<Ingredient> inputs = NonNullList.of(
                    Ingredient.EMPTY,
                    Ingredient.of(input),
                    Ingredient.of(ModItemTags.CRATE_LOCKING_ITEMS)
            );

            ResourceLocation loc = Condiments.loc("/crate_coloring_" + "crate_locking_" + input.getDescriptionId());
            //? if < 1.21 {
            /*recipeList.add(new ShapelessRecipe(loc, group, CraftingBookCategory.MISC, output, inputs));
            *///?} else
            recipeList.add(new RecipeHolder<>(loc, new ShapelessRecipe(group, CraftingBookCategory.MISC, output, inputs)));
        }

        return recipeList;
    }

    //? if < 1.21 {
    /*public static List<CraftingRecipe> crateUnlockingRecipe() {
        ArrayList<CraftingRecipe> recipeList = new ArrayList<>();
    *///?} else {
    public static List<RecipeHolder<CraftingRecipe>> crateUnlockingRecipe() {
        ArrayList<RecipeHolder<CraftingRecipe>> recipeList = new ArrayList<>();
    //?}
        String group = "crate_locking";
        Ingredient ingredients = Ingredient.of(ModItemTags.CRATES);

        for (ItemStack output : ingredients.getItems()) {
            ItemStack input = output.copy();
            new CrateContents(null, 0, true).updateCrateItem(input);

            NonNullList<Ingredient> inputs = NonNullList.of(
                    Ingredient.EMPTY,
                    Ingredient.of(input),
                    Ingredient.of(ModItemTags.CRATE_UNLOCKING_ITEMS)
            );

            ResourceLocation loc = Condiments.loc("/crate_unlocking_" + input.getDescriptionId());
            //? if < 1.21 {
            /*recipeList.add(new ShapelessRecipe(loc, group, CraftingBookCategory.MISC, output, inputs));
            *///?} else
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
