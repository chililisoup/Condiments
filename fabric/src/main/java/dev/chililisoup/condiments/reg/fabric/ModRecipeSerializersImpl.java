package dev.chililisoup.condiments.reg.fabric;

import dev.chililisoup.condiments.Condiments;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Supplier;

public class ModRecipeSerializersImpl {
    public static <S extends RecipeSerializer<T>, T extends Recipe<?>> Supplier<S> register(String name, S recipeSerializer) {
        S serializer = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, Condiments.loc(name), recipeSerializer);
        return () -> serializer;
    }
}
