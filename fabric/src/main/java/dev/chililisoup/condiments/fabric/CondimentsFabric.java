package dev.chililisoup.condiments.fabric;

import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.reg.ModBlockEntities;
import dev.chililisoup.condiments.reg.ModDispenserBehaviors;
import net.fabricmc.api.ModInitializer;

public class CondimentsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Condiments.init();
        ModBlockEntities.init();
        ModDispenserBehaviors.init();
    }
}