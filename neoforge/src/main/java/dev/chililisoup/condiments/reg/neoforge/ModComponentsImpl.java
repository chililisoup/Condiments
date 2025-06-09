package dev.chililisoup.condiments.reg.neoforge;

import dev.chililisoup.condiments.Condiments;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ModComponentsImpl {
    public static final DeferredRegister<DataComponentType<?>> COMPONENT_TYPES = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, Condiments.MOD_ID);

    public static <T> Supplier<DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        return COMPONENT_TYPES.register(name, () -> (builder.apply(DataComponentType.builder())).build());
    }
}

