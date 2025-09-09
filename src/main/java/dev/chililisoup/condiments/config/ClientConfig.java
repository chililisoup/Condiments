package dev.chililisoup.condiments.config;

import dev.chililisoup.condiments.Condiments;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigBuilder;
import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigType;

//? if < 1.21 {
/*import net.mehvahdjukaar.moonlight.api.platform.configs.ConfigSpec;
*///?} else
import net.mehvahdjukaar.moonlight.api.platform.configs.ModConfigHolder;

import java.util.function.Supplier;

//$ client_only
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class ClientConfig {
    public static final Supplier<Boolean> SHOW_CRATE_HUD;
    public static final Supplier<Double> CRATE_HUD_X_ALIGNMENT;
    public static final Supplier<Integer> CRATE_HUD_X_OFFSET;
    public static final Supplier<Double> CRATE_HUD_Y_ALIGNMENT;
    public static final Supplier<Integer> CRATE_HUD_Y_OFFSET;
    public static final Supplier<Boolean> RENDER_CRATE_CONTENTS_IN_HAND;

    //? if < 1.21 {
    /*public static final ConfigSpec CONFIG_SPEC;
    *///?} else
    public static final ModConfigHolder CONFIG_SPEC;

    public static void init() {}

    static {
        ConfigBuilder builder = ConfigBuilder.create(Condiments.MOD_ID, ConfigType.CLIENT);

        builder.push("crates");
        SHOW_CRATE_HUD = builder.comment("Should the crate HUD render")
                .define("show_crate_hud", true);
        CRATE_HUD_X_ALIGNMENT = builder.comment("Crate HUD horizontal screen alignment (0: left, 1: right)")
                .define("crate_hud_x_alignment", 0.0, 0, 1);
        CRATE_HUD_X_OFFSET = builder.comment("Crate HUD horizontal pixel offset")
                .define("crate_hud_x_offset", 1, -100, 100);
        CRATE_HUD_Y_ALIGNMENT = builder.comment("Crate HUD vertical screen alignment (0: top, 1: bottom)")
                .define("crate_hud_y_alignment", 1.0, 0, 1);
        CRATE_HUD_Y_OFFSET = builder.comment("Crate HUD vertical pixel offset")
                .define("crate_hud_y_offset", -1, -100, 100);
        RENDER_CRATE_CONTENTS_IN_HAND = builder
                .comment("Render crate contents instead of crate in first person hand when crate would place contained block")
                .define("render_crate_contents_in_hand", true);
        builder.pop();

        //? if < 1.21 {
        /*CONFIG_SPEC = builder.buildAndRegister();
        CONFIG_SPEC.loadFromFile();
        *///?} else {
        CONFIG_SPEC = builder.build();
        //?}
    }
}
