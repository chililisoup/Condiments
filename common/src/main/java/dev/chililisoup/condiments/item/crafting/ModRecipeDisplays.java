package dev.chililisoup.condiments.item.crafting;

import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.block.CrateBlock;
import dev.chililisoup.condiments.reg.ModItemTags;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import java.util.ArrayList;
import java.util.List;

public class ModRecipeDisplays {
    public static List<CraftingRecipe> crateColoringRecipe() {
        ArrayList<CraftingRecipe> recipeList = new ArrayList<>();
        String group = "crate_coloring";
        Ingredient ingredients = Ingredient.of(ModItemTags.CRATES);

        for (DyeColor color : DyeColor.values()) {
            DyeItem dye = DyeItem.byColor(color);
            ItemStack output = CrateBlock.getColoredItemStack(color);

            NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, ingredients, Ingredient.of(dye));

            ResourceLocation loc = new ResourceLocation(Condiments.MOD_ID, "crate_coloring_" + color.getName());
            recipeList.add(new ShapelessRecipe(loc, group, CraftingBookCategory.MISC, output, inputs));
        }
        return recipeList;
    }

    public static List<CraftingRecipe> crateLockingRecipe() {
        ArrayList<CraftingRecipe> recipeList = new ArrayList<>();
        String group = "crate_locking";
        Ingredient ingredients = Ingredient.of(ModItemTags.CRATES);

        for (ItemStack input : ingredients.getItems()) {
            ItemStack output = input.copy();

            CompoundTag compoundTag = output.getOrCreateTag().getCompound("BlockEntityTag");
            compoundTag.putBoolean("CrateLocked", true);
            output.getOrCreateTag().put("BlockEntityTag", compoundTag);

            NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, Ingredient.of(input), Ingredient.of(Items.REDSTONE_TORCH));

            ResourceLocation loc = new ResourceLocation(Condiments.MOD_ID, "crate_locking_" + input.getDescriptionId());
            recipeList.add(new ShapelessRecipe(loc, group, CraftingBookCategory.MISC, output, inputs));
        }

        return recipeList;
    }

    public static List<CraftingRecipe> crateUnlockingRecipe() {
        ArrayList<CraftingRecipe> recipeList = new ArrayList<>();
        String group = "crate_locking";
        Ingredient ingredients = Ingredient.of(ModItemTags.CRATES);

        for (ItemStack output : ingredients.getItems()) {
            ItemStack input = output.copy();

            CompoundTag compoundTag = input.getOrCreateTag().getCompound("BlockEntityTag");
            compoundTag.putBoolean("CrateLocked", true);
            input.getOrCreateTag().put("BlockEntityTag", compoundTag);

            NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, Ingredient.of(input), Ingredient.of(Items.STICK));

            ResourceLocation loc = new ResourceLocation(Condiments.MOD_ID, "crate_unlocking_" + input.getDescriptionId());
            recipeList.add(new ShapelessRecipe(loc, group, CraftingBookCategory.MISC, output, inputs));
        }

        return recipeList;
    }
}
