package dev.chililisoup.condiments.reg;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.chililisoup.condiments.item.component.CrateContents;
import net.minecraft.core.component.DataComponentType;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ModComponents {
    public static Supplier<DataComponentType<CrateContents>> CRATE_CONTENTS;

    public static void init() {
        CRATE_CONTENTS = register("crate_contents", builder -> builder.persistent(CrateContents.CODEC).networkSynchronized(CrateContents.STREAM_CODEC).cacheEncoding());
    }

    @ExpectPlatform
    private static <T> Supplier<DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        throw new AssertionError();
    }
}
