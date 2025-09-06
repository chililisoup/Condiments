//? if > 1.21 {
package dev.chililisoup.condiments.reg;

import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.block.entity.CrateContents;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.core.component.DataComponentType;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ModComponents {
    public static final Supplier<DataComponentType<CrateContents>> CRATE_CONTENTS;

    public static void init() {}

    static {
        CRATE_CONTENTS = register(
                "crate_contents",
                builder -> builder.persistent(CrateContents.CODEC).networkSynchronized(CrateContents.STREAM_CODEC).cacheEncoding()
        );
    }

    private static <T> Supplier<DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        return RegHelper.registerDataComponent(
                Condiments.loc(name),
                () -> (builder.apply(DataComponentType.builder())).build()
        );
    }
}
//?}