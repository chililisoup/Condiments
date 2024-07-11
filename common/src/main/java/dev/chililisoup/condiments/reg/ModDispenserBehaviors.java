package dev.chililisoup.condiments.reg;

import dev.chililisoup.condiments.extra.CrateDispenserBehavior;
import net.minecraft.world.level.block.DispenserBlock;

public class ModDispenserBehaviors {
    public static void init() {
        DispenserBlock.registerBehavior(ModBlocks.CRATE.get().asItem(), new CrateDispenserBehavior());
        DispenserBlock.registerBehavior(ModBlocks.WHITE_CRATE.get().asItem(), new CrateDispenserBehavior());
        DispenserBlock.registerBehavior(ModBlocks.LIGHT_GRAY_CRATE.get().asItem(), new CrateDispenserBehavior());
        DispenserBlock.registerBehavior(ModBlocks.GRAY_CRATE.get().asItem(), new CrateDispenserBehavior());
        DispenserBlock.registerBehavior(ModBlocks.BLACK_CRATE.get().asItem(), new CrateDispenserBehavior());
        DispenserBlock.registerBehavior(ModBlocks.BROWN_CRATE.get().asItem(), new CrateDispenserBehavior());
        DispenserBlock.registerBehavior(ModBlocks.RED_CRATE.get().asItem(), new CrateDispenserBehavior());
        DispenserBlock.registerBehavior(ModBlocks.ORANGE_CRATE.get().asItem(), new CrateDispenserBehavior());
        DispenserBlock.registerBehavior(ModBlocks.YELLOW_CRATE.get().asItem(), new CrateDispenserBehavior());
        DispenserBlock.registerBehavior(ModBlocks.LIME_CRATE.get().asItem(), new CrateDispenserBehavior());
        DispenserBlock.registerBehavior(ModBlocks.GREEN_CRATE.get().asItem(), new CrateDispenserBehavior());
        DispenserBlock.registerBehavior(ModBlocks.CYAN_CRATE.get().asItem(), new CrateDispenserBehavior());
        DispenserBlock.registerBehavior(ModBlocks.LIGHT_BLUE_CRATE.get().asItem(), new CrateDispenserBehavior());
        DispenserBlock.registerBehavior(ModBlocks.BLUE_CRATE.get().asItem(), new CrateDispenserBehavior());
        DispenserBlock.registerBehavior(ModBlocks.PURPLE_CRATE.get().asItem(), new CrateDispenserBehavior());
        DispenserBlock.registerBehavior(ModBlocks.MAGENTA_CRATE.get().asItem(), new CrateDispenserBehavior());
        DispenserBlock.registerBehavior(ModBlocks.PINK_CRATE.get().asItem(), new CrateDispenserBehavior());
    }
}
