//? if fabric {
package dev.chililisoup.condiments.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.config.ClientConfig;
import dev.chililisoup.condiments.config.CommonConfig;
import dev.chililisoup.condiments.reg.ModBlocks;
import net.mehvahdjukaar.moonlight.api.platform.configs.fabric.FabricConfigListScreen;
import net.minecraft.network.chat.Component;

public class ModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new FabricConfigListScreen(
                Condiments.MOD_ID,
                ModBlocks.CRATE.get().asItem().getDefaultInstance(),
                Component.literal("Condiments Configs"),
                null,
                parent,
                CommonConfig.CONFIG_SPEC,
                ClientConfig.CONFIG_SPEC
        );
    }
}
//?}