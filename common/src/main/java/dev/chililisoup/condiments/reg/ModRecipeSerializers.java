package dev.chililisoup.condiments.reg;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.chililisoup.condiments.item.crafting.CrateColoring;
import dev.chililisoup.condiments.item.crafting.CrateLocking;
import dev.chililisoup.condiments.item.crafting.CrateUnlocking;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

import java.util.function.Supplier;

public class ModRecipeSerializers {
    public static Supplier<RecipeSerializer<CrateColoring>> CRATE_COLORING;
    public static Supplier<RecipeSerializer<CrateLocking>> CRATE_LOCKING;
    public static Supplier<RecipeSerializer<CrateUnlocking>> CRATE_UNLOCKING;

    public static void init() {
        CRATE_COLORING = register("crate_coloring", new SimpleCraftingRecipeSerializer<>(CrateColoring::new));
        CRATE_LOCKING = register("crate_locking", new SimpleCraftingRecipeSerializer<>(CrateLocking::new));
        CRATE_UNLOCKING = register("crate_unlocking", new SimpleCraftingRecipeSerializer<>(CrateUnlocking::new));
    }

    @ExpectPlatform
    private static <S extends RecipeSerializer<T>, T extends Recipe<?>> Supplier<S> register(String name, S recipeSerializer) {
        throw new AssertionError();
    }
}
