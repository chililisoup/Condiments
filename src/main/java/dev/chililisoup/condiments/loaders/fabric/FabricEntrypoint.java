//? if fabric {
package dev.chililisoup.condiments.loaders.fabric;

import dev.chililisoup.condiments.Condiments;
import net.fabricmc.api.ModInitializer;

public class FabricEntrypoint implements ModInitializer {
    @Override
    public void onInitialize() {
        Condiments.init();
    }
}
//?}