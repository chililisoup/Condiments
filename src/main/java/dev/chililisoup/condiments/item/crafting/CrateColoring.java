package dev.chililisoup.condiments.item.crafting;

import dev.chililisoup.condiments.block.CrateBlock;
import dev.chililisoup.condiments.item.CrateItem;
import dev.chililisoup.condiments.reg.ModRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class CrateColoring extends CustomRecipe {
    public CrateColoring(CraftingBookCategory category) {
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
                    if (!(itemStack.getItem() instanceof DyeItem)) return false;
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
        DyeItem dyeItem = (DyeItem) Items.WHITE_DYE;

        for (int i = 0; i < input.size(); ++i) {
            ItemStack itemStack2 = input.getItem(i);
            if (!itemStack2.isEmpty()) {
                Item item = itemStack2.getItem();
                if (item instanceof CrateItem) itemStack = itemStack2;
                else if (item instanceof DyeItem) dyeItem = (DyeItem) item;
            }
        }

        Block block = CrateBlock.getBlockByColor(dyeItem.getDyeColor());
        return itemStack.transmuteCopy(block, 1);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.CRATE_COLORING.get();
    }
}
