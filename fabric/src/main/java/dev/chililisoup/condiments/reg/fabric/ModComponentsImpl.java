package dev.chililisoup.condiments.reg.fabric;

import dev.chililisoup.condiments.Condiments;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ModComponentsImpl {
    public static <T> Supplier<DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        DataComponentType<T> component = Registry.register(
                BuiltInRegistries.DATA_COMPONENT_TYPE,
                ResourceLocation.fromNamespaceAndPath(Condiments.MOD_ID, name),
                (builder.apply(DataComponentType.builder())).build()
        );
        return () -> component;
    }
}
