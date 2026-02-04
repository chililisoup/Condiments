package dev.chililisoup.condiments.config;

import dev.chililisoup.condiments.Condiments;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigBuilder;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigType;

//? if < 1.21 {
/*import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigSpec;
*///?} else
import net.mehvahdjukaar.moonlight.api.platform.configs.ModConfigHolder;

import java.util.HashMap;
import java.util.function.Supplier;

public class CommonConfig {
    private static final HashMap<String, Supplier<Boolean>> FEATURE_TOGGLES = new HashMap<>();
    public static final Supplier<Boolean> WOOD_WALLS;
    public static final Supplier<Boolean> WOOD_ACCENTS;
    public static final Supplier<Boolean> POLISHED_WOOD;
    public static final Supplier<Boolean> CRATES;
    public static final Supplier<Boolean> RAIL_INTERSECTION;
    public static final Supplier<Boolean> ANALOG_RAIL;
    public static final Supplier<Boolean> BLACKENED_IRON;
    public static final Supplier<Boolean> REDSTONE_LED;
    public static final Supplier<Boolean> SAUCER_LIGHT;
    public static final Supplier<Boolean> BRAZIER;

    public static final Supplier<Integer> CRATE_MAX_CONTAINED_STACKS;
    public static final Supplier<Integer> EMPTY_CRATE_STACK_SIZE;
    public static final Supplier<Boolean> CRATES_CONTAIN_EMPTY_CRATES;

    public static final Supplier<Boolean> TINTED_GLASS_TERMINATES_BEACONS;
    public static final Supplier<Double> ANALOG_RAIL_MAX_SPEED;
    public static final Supplier<Boolean> COPPER_FIRE;

    //? if < 1.21 {
    /*public static final ConfigSpec CONFIG_SPEC;
    *///?} else
    public static final ModConfigHolder CONFIG_SPEC;

    private static final Supplier<Boolean> TRUE = () -> true;

    public static void init() {}

    public static boolean isEnabled(String key) {
        return FEATURE_TOGGLES.getOrDefault(key, TRUE).get();
    }

    static {
        ConfigBuilder builder = ConfigBuilder.create(Condiments.MOD_ID, ConfigType./*? if < 1.21 {*/ /*COMMON *//*?} else {*/ COMMON_SYNCED /*?}*/);

        builder.push("feature_toggles");
        WOOD_WALLS = featureToggle(builder, "wood_walls");
        WOOD_ACCENTS = featureToggle(builder, "wood_accents");
        POLISHED_WOOD = featureToggle(builder, "polished_wood");
        CRATES = featureToggle(builder, "crates");
        RAIL_INTERSECTION = featureToggle(builder, "rail_intersection");
        ANALOG_RAIL = featureToggle(builder, "analog_rail");
        BLACKENED_IRON = featureToggle(builder, "blackened_iron");
        REDSTONE_LED = featureToggle(builder, "redstone_led");
        SAUCER_LIGHT = featureToggle(builder, "saucer_light");
        BRAZIER = featureToggle(builder, "brazier");
        builder.pop();

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
        ANALOG_RAIL_MAX_SPEED = builder.comment("Analog rail max speed (blocks per tick, before natural slowdown)")
                .define("analog_rail_max_speed", 0.6, 0.1, 16.0);
        COPPER_FIRE = builder.comment("If fire on copper blocks should turn into a green copper fire")
                .define("copper_fire", true);
        builder.pop();

        //? if < 1.21 {
        /*builder.setSynced();
        CONFIG_SPEC = builder.buildAndRegister();
        CONFIG_SPEC.loadFromFile();
        *///?} else {
        CONFIG_SPEC = builder.build();
        CONFIG_SPEC.forceLoad();
        //?}
    }

    private static Supplier<Boolean> featureToggle(ConfigBuilder builder, String key) {
        Supplier<Boolean> config = builder.define(key, true);
        FEATURE_TOGGLES.put(key, config);
        return config;
    }
}
