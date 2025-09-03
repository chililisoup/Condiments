package dev.chililisoup.condiments;

import dev.chililisoup.condiments.config.ClientConfig;
import dev.chililisoup.condiments.dynamicpack.ClientDynamicResourcesGenerator;
import dev.chililisoup.condiments.reg.ClientRegistry;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;

public class CondimentsClient {
    public static void init() {
        ClientConfig.init();
        ClientRegistry.init();
        RegHelper.registerDynamicResourceProvider(ClientDynamicResourcesGenerator.getInstance());
    }
}
