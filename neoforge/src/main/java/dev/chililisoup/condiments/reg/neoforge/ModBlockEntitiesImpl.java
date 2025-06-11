package dev.chililisoup.condiments.reg.neoforge;

import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.block.entity.CrateBlockEntity;
import dev.chililisoup.condiments.reg.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntitiesImpl {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Condiments.MOD_ID);

    public static Supplier<BlockEntityType<CrateBlockEntity>> addCrateBlockEntity() {
        return BLOCK_ENTITY_TYPES.register("crate", () ->
                BlockEntityType.Builder.of(
                        CrateBlockEntity::new,
                        ModBlocks.getCrates()
                ).build(null));
    }
}
