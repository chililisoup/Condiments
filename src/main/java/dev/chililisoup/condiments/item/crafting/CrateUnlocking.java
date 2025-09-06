package dev.chililisoup.condiments.item.crafting;

import dev.chililisoup.condiments.extra.VersionHelper;
import dev.chililisoup.condiments.item.CrateItem;
import dev.chililisoup.condiments.block.entity.CrateContents;
import dev.chililisoup.condiments.reg.ModRecipeSerializers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

//? if < 1.21 {
/*import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.core.RegistryAccess;
*///?} else {
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.crafting.CraftingInput;
//?}

public class CrateUnlocking extends CustomRecipe {
    //? if < 1.21 {
    /*public CrateUnlocking(ResourceLocation id, CraftingBookCategory category) { super(id, category); }
    *///?} else
    public CrateUnlocking(CraftingBookCategory category) { super(category); }

    @Override
    //? if < 1.21 {
    /*public boolean matches(CraftingContainer input, Level level) {
    *///?} else
    public boolean matches(CraftingInput input, Level level) {
        int i = 0;
        int j = 0;

        for (int k = 0; k < VersionHelper.size(input); ++k) {
            ItemStack itemStack = input.getItem(k);
            if (!itemStack.isEmpty()) {
                if (itemStack.getItem() instanceof CrateItem) ++i;
                else {
                    if (!(itemStack.is(Items.STICK))) return false;
                    ++j;
                }
                if (j > 1 || i > 1) return false;
            }
        }

        return i == 1 && j == 1;
    }

    @Override
    //? if < 1.21 {
    /*public @NotNull ItemStack assemble(CraftingContainer input, RegistryAccess registries) {
    *///?} else
    public @NotNull ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack itemStack = ItemStack.EMPTY;

        for (int i = 0; i < VersionHelper.size(input); ++i) {
            ItemStack itemStack2 = input.getItem(i);
            if (!itemStack2.isEmpty() && itemStack2.getItem() instanceof CrateItem) {
                itemStack = itemStack2.copyWithCount(1);
                break;
            }
        }

        CrateContents crateContents = CrateContents.fromCrateItem(itemStack);
        CrateContents.Mutable mutable = crateContents.toMutable();
        mutable.setLocked(false);
        mutable.toImmutable().updateCrateItem(itemStack);

        return itemStack;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.CRATE_UNLOCKING.get();
    }
}
