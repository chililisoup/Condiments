package dev.chililisoup.condiments.reg;

import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.item.crafting.CrateColoring;
import dev.chililisoup.condiments.item.crafting.CrateLocking;
import dev.chililisoup.condiments.item.crafting.CrateUnlocking;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

import java.util.function.Supplier;

public class ModRecipeSerializers {
    public static final Supplier<RecipeSerializer<CrateColoring>> CRATE_COLORING;
    public static final Supplier<RecipeSerializer<CrateLocking>> CRATE_LOCKING;
    public static final Supplier<RecipeSerializer<CrateUnlocking>> CRATE_UNLOCKING;

    public static void init() {}

    static {
        CRATE_COLORING = register("crate_coloring", new SimpleCraftingRecipeSerializer<>(CrateColoring::new));
        CRATE_LOCKING = register("crate_locking", new SimpleCraftingRecipeSerializer<>(CrateLocking::new));
        CRATE_UNLOCKING = register("crate_unlocking", new SimpleCraftingRecipeSerializer<>(CrateUnlocking::new));
    }

    private static <S extends RecipeSerializer<T>, T extends Recipe<?>> Supplier<S> register(String name, S recipeSerializer) {
        return RegHelper.register(Condiments.loc(name), () -> recipeSerializer, Registries.RECIPE_SERIALIZER);
    }
}
