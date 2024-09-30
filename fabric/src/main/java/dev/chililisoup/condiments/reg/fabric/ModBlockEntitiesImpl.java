package dev.chililisoup.condiments.reg.fabric;

import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.block.entity.CrateBlockEntity;
import dev.chililisoup.condiments.reg.ModBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class ModBlockEntitiesImpl {
    public static Supplier<BlockEntityType<CrateBlockEntity>> addCrateBlockEntity() {
        BlockEntityType<CrateBlockEntity> blockEntityType = BlockEntityType.Builder.of(
                CrateBlockEntity::new,
                ModBlocks.CRATE.get(),
                ModBlocks.WHITE_CRATE.get(),
                ModBlocks.LIGHT_GRAY_CRATE.get(),
                ModBlocks.GRAY_CRATE.get(),
                ModBlocks.BLACK_CRATE.get(),
                ModBlocks.BROWN_CRATE.get(),
                ModBlocks.RED_CRATE.get(),
                ModBlocks.ORANGE_CRATE.get(),
                ModBlocks.YELLOW_CRATE.get(),
                ModBlocks.LIME_CRATE.get(),
                ModBlocks.GREEN_CRATE.get(),
                ModBlocks.CYAN_CRATE.get(),
                ModBlocks.LIGHT_BLUE_CRATE.get(),
                ModBlocks.BLUE_CRATE.get(),
                ModBlocks.PURPLE_CRATE.get(),
                ModBlocks.MAGENTA_CRATE.get(),
                ModBlocks.PINK_CRATE.get()
        ).build(null);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Condiments.MOD_ID, "crate"), blockEntityType);
        return () -> blockEntityType;
    }
}
