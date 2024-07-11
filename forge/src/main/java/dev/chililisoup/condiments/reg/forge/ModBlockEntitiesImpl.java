package dev.chililisoup.condiments.reg.forge;

import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.block.entity.CrateBlockEntity;
import dev.chililisoup.condiments.reg.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModBlockEntitiesImpl {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Condiments.MOD_ID);

    public static Supplier<BlockEntityType<CrateBlockEntity>> addCrateBlockEntity() {
        return BLOCK_ENTITY_TYPES.register("crate", () ->
                BlockEntityType.Builder.of(
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
                ).build(null));
    }
}
