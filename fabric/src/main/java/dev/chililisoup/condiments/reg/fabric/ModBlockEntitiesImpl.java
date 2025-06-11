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
                ModBlocks.getCrates()
        ).build(null);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(Condiments.MOD_ID, "crate"), blockEntityType);
        return () -> blockEntityType;
    }
}
