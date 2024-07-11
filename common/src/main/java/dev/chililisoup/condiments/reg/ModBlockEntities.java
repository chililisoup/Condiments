package dev.chililisoup.condiments.reg;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.chililisoup.condiments.block.entity.CrateBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static Supplier<BlockEntityType<CrateBlockEntity>> CRATE_BE_TYPE;

    public static void init() {
        CRATE_BE_TYPE = addCrateBlockEntity();
    }

    @ExpectPlatform
    public static Supplier<BlockEntityType<CrateBlockEntity>> addCrateBlockEntity() {
        throw new AssertionError();
    }
}
