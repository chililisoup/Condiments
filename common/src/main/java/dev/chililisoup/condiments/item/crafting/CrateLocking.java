package dev.chililisoup.condiments.item.crafting;

import dev.chililisoup.condiments.item.CrateItem;
import dev.chililisoup.condiments.item.component.CrateContents;
import dev.chililisoup.condiments.reg.ModComponents;
import dev.chililisoup.condiments.reg.ModRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class CrateLocking extends CustomRecipe {
    public CrateLocking(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        int i = 0;
        int j = 0;

        for (int k = 0; k < input.size(); ++k) {
            ItemStack itemStack = input.getItem(k);
            if (!itemStack.isEmpty()) {
                if (itemStack.getItem() instanceof CrateItem) ++i;
                else {
                    if (!(itemStack.is(Items.REDSTONE_TORCH))) return false;
                    ++j;
                }
                if (j > 1 || i > 1) return false;
            }
        }

        return i == 1 && j == 1;
    }

    @Override
    public @NotNull ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack itemStack = ItemStack.EMPTY;

        for (int i = 0; i < input.size(); ++i) {
            ItemStack itemStack2 = input.getItem(i);
            if (!itemStack2.isEmpty() && itemStack2.getItem() instanceof CrateItem) {
                itemStack = itemStack2.copyWithCount(1);
                break;
            }
        }

        CrateContents crateContents = itemStack.getOrDefault(ModComponents.CRATE_CONTENTS.get(), CrateContents.EMPTY);
        CrateContents.Mutable mutable = new CrateContents.Mutable(crateContents);
        mutable.setLocked(true);
        itemStack.set(ModComponents.CRATE_CONTENTS.get(), mutable.toImmutable());

        return itemStack;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.CRATE_LOCKING.get();
    }
}
