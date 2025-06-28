package dev.chililisoup.condiments.config;

import dev.chililisoup.condiments.Condiments;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigBuilder;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigType;
import net.mehvahdjukaar.moonlight.api.platform.configs.ModConfigHolder;

import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ClientConfig {
    public static final Supplier<Boolean> SHOW_CRATE_HUD;
    public static final Supplier<Float> CRATE_HUD_X_ALIGNMENT;
    public static final Supplier<Integer> CRATE_HUD_X_OFFSET;
    public static final Supplier<Float> CRATE_HUD_Y_ALIGNMENT;
    public static final Supplier<Integer> CRATE_HUD_Y_OFFSET;

    public static final ModConfigHolder CONFIG_SPEC;

    public static void init() {}

    static {
        ConfigBuilder builder = ConfigBuilder.create(Condiments.MOD_ID, ConfigType.CLIENT);

        builder.push("crates");
        SHOW_CRATE_HUD = builder.comment("Should the crate HUD render")
                .define("show_crate_hud", true);
        CRATE_HUD_X_ALIGNMENT = builder.comment("Crate HUD horizontal screen alignment (0: left, 1: right)")
                .define("crate_hud_x_alignment", 0F, 0, 1);
        CRATE_HUD_X_OFFSET = builder.comment("Crate HUD horizontal pixel offset")
                .define("crate_hud_x_offset", 1, -100, 100);
        CRATE_HUD_Y_ALIGNMENT = builder.comment("Crate HUD vertical screen alignment (0: top, 1: bottom)")
                .define("crate_hud_y_alignment", 1F, 0, 1);
        CRATE_HUD_Y_OFFSET = builder.comment("Crate HUD vertical pixel offset")
                .define("crate_hud_y_offset", -1, -100, 100);
        builder.pop();

        CONFIG_SPEC = builder.build();
    }
}
