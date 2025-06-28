package dev.chililisoup.condiments.fabric;

import dev.chililisoup.condiments.Condiments;
import net.fabricmc.api.ModInitializer;

public class CondimentsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Condiments.init();
    }
}