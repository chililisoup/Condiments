package dev.chililisoup.condiments.compat;

import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.item.crafting.ModRecipeDisplays;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class JeiCompat implements IModPlugin {
    private static final ResourceLocation LOC = Condiments.loc("jei_compat");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return LOC;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        registry.addRecipes(RecipeTypes.CRAFTING, ModRecipeDisplays.getAll());

        ModRecipeDisplays.ingredientInfos().forEach((ingredients, infos) ->
                registry.addItemStackInfo(
                        ingredients.stream().map(ingredient -> ingredient.asItem().getDefaultInstance()).toList(),
                        infos.stream().map(Component::translatable).toArray(Component[]::new)
                )
        );
    }
}
