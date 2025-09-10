//? if fabric {
package dev.chililisoup.condiments.loaders.fabric;

import dev.chililisoup.condiments.Condiments;
import net.fabricmc.api.ModInitializer;

//? if < 1.21 {
/*import dev.chililisoup.condiments.compat.create.CreateCompat;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
*///?}

public class FabricEntrypoint implements ModInitializer {
    @Override
    public void onInitialize() {
        Condiments.init();

        //? if < 1.21 {
        /*if (PlatHelper.isModLoaded("create"))
            PlatHelper.addCommonSetup(CreateCompat::init);
        *///?}
    }
}
//?}