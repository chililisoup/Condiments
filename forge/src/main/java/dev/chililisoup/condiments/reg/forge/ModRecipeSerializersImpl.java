package dev.chililisoup.condiments.reg.forge;

import dev.chililisoup.condiments.Condiments;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModRecipeSerializersImpl {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Condiments.MOD_ID);

    public static <S extends RecipeSerializer<T>, T extends Recipe<?>> Supplier<S> register(String name, S recipeSerializer) {
        return RECIPE_SERIALIZERS.register(name, () -> recipeSerializer);
    }
}
