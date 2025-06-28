package dev.chililisoup.condiments.config;

import dev.chililisoup.condiments.Condiments;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigBuilder;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigType;
import net.mehvahdjukaar.moonlight.api.platform.configs.ModConfigHolder;

import java.util.function.Supplier;

public class CommonConfig {
    public static final Supplier<Integer> CRATE_MAX_CONTAINED_STACKS;
    public static final Supplier<Integer> EMPTY_CRATE_STACK_SIZE;
    public static final Supplier<Boolean> CRATES_CONTAIN_EMPTY_CRATES;

    public static final Supplier<Boolean> TINTED_GLASS_TERMINATES_BEACONS;

    public static final ModConfigHolder CONFIG_SPEC;

    public static void init() {}

    static {
        ConfigBuilder builder = ConfigBuilder.create(Condiments.MOD_ID, ConfigType.COMMON_SYNCED);

        builder.push("crates");
        CRATE_MAX_CONTAINED_STACKS = builder.comment("How many full stacks of an item each crate can hold")
                .define("crate_max_contained_stacks", 64, 1, 256);
        EMPTY_CRATE_STACK_SIZE = builder.comment("Stack size of empty crates")
                .gameRestart()
                .define("empty_crate_stack_size", 64, 1, 64);
        CRATES_CONTAIN_EMPTY_CRATES = builder.comment("If crates are allowed to contain empty crates")
                .define("crates_contain_empty_crates", true);
        builder.pop();

        builder.push("misc");
        TINTED_GLASS_TERMINATES_BEACONS = builder.comment("If beacon beams stop at tinted glass (and exist up to that point)")
                .define("tinted_glass_terminates_beacons", true);
        builder.pop();

        CONFIG_SPEC = builder.build();
        CONFIG_SPEC.forceLoad();
    }
}
