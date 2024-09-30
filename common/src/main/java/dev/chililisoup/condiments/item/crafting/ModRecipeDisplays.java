package dev.chililisoup.condiments.item.crafting;

import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.block.CrateBlock;
import dev.chililisoup.condiments.reg.ModItemTags;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.*;

import java.util.ArrayList;
import java.util.List;

public class ModRecipeDisplays {
    public static List<RecipeHolder<CraftingRecipe>> crateColoringRecipe() {
        ArrayList<RecipeHolder<CraftingRecipe>> recipeList = new ArrayList<>();
        String group = "crate_coloring";
        Ingredient ingredients = Ingredient.of(ModItemTags.CRATES);

        for (DyeColor color : DyeColor.values()) {
            DyeItem dye = DyeItem.byColor(color);
            ItemStack output = CrateBlock.getColoredItemStack(color);

            NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, ingredients, Ingredient.of(dye));

            ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(Condiments.MOD_ID, "crate_coloring_" + color.getName());
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

            CompoundTag compoundTag = output.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).copyTag();
            compoundTag.putBoolean("CrateLocked", true);
            output.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(compoundTag));

            NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, Ingredient.of(input), Ingredient.of(Items.REDSTONE_TORCH));

            ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(Condiments.MOD_ID, "crate_locking_" + input.getDescriptionId());
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

            CompoundTag compoundTag = input.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).copyTag();
            compoundTag.putBoolean("CrateLocked", true);
            input.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(compoundTag));

            NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, Ingredient.of(input), Ingredient.of(Items.STICK));

            ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(Condiments.MOD_ID, "crate_unlocking_" + input.getDescriptionId());
            recipeList.add(new RecipeHolder<>(loc, new ShapelessRecipe(group, CraftingBookCategory.MISC, output, inputs)));
        }

        return recipeList;
    }
}
