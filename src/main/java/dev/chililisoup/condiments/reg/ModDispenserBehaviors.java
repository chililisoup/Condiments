package dev.chililisoup.condiments.reg;

import dev.chililisoup.condiments.extra.CrateDispenserBehavior;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.world.level.block.Block;

public class ModDispenserBehaviors {
    public static void init() {
        RegHelper.addDynamicDispenserBehaviorRegistration(event -> {
            for (Block block : ModBlocks.getCrates())
                event.register(block.asItem(), new CrateDispenserBehavior());
        });
    }
}
