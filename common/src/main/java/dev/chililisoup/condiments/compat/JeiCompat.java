package dev.chililisoup.condiments.compat;

import com.mojang.datafixers.util.Pair;
import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.item.crafting.ModRecipeDisplays;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JeiPlugin
public class JeiCompat implements IModPlugin {
    private static final ResourceLocation loc = new ResourceLocation(Condiments.MOD_ID, "jei_compat");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return loc;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        registry.addRecipes(RecipeTypes.CRAFTING, ModRecipeDisplays.crateColoringRecipe());
        registry.addRecipes(RecipeTypes.CRAFTING, ModRecipeDisplays.crateLockingRecipe());
        registry.addRecipes(RecipeTypes.CRAFTING, ModRecipeDisplays.crateUnlockingRecipe());
    }
}
