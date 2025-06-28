package dev.chililisoup.condiments.reg;

import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.world.level.block.Block;

public class ModDispenserBehaviors {
    public static void init() {
        RegHelper.addDynamicDispenserBehaviorRegistration(event -> {
            for (Block block : ModBlocks.getCrates())
                event.registerPlaceBlock(block);
        });
    }
}
