package dev.chililisoup.condiments.item.crafting;

import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.item.CrateItem;
import dev.chililisoup.condiments.reg.ModRecipeSerializers;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class CrateLocking extends CustomRecipe {
    public CrateLocking(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer inv, Level level) {
        int i = 0;
        int j = 0;

        for (int k = 0; k < inv.getContainerSize(); ++k) {
            ItemStack itemStack = inv.getItem(k);
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
    public @NotNull ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack itemStack = ItemStack.EMPTY;

        for (int i = 0; i < container.getContainerSize(); ++i) {
            ItemStack itemStack2 = container.getItem(i);
            if (!itemStack2.isEmpty() && itemStack2.getItem() instanceof CrateItem) {
                itemStack = itemStack2.copy();
                break;
            }
        }

        CompoundTag compoundTag = itemStack.getOrCreateTag().getCompound("BlockEntityTag");
        compoundTag.putBoolean("CrateLocked", true);
        itemStack.getOrCreateTag().put("BlockEntityTag", compoundTag);

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
