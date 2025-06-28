package dev.chililisoup.condiments.reg;

import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.block.entity.CrateBlockEntity;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static Supplier<BlockEntityType<CrateBlockEntity>> CRATE_BE_TYPE;

    public static void init() {
        CRATE_BE_TYPE = RegHelper.registerBlockEntityType(
                Condiments.loc("crate"),
                () -> PlatHelper.newBlockEntityType(
                        CrateBlockEntity::new,
                        ModBlocks.getCrates()
                )
        );
    }
}
