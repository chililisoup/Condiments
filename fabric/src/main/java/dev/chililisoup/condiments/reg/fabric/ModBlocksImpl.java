package dev.chililisoup.condiments.reg.fabric;

import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.reg.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.function.Supplier;

public class ModBlocksImpl {
    public static final ArrayList<BlockParams> BlocksRegistry = new ArrayList<>();


    public static Supplier<Block> addBlock(ModBlocks.Params params) {
        ResourceLocation loc = new ResourceLocation(Condiments.MOD_ID, params.id);
        Block block = params.blockFactory.get();
        BlockItem item = params.getItem(block);

        Registry.register(BuiltInRegistries.BLOCK, loc, block);
        Registry.register(BuiltInRegistries.ITEM, loc, item);

        BlocksRegistry.add(new BlockParams(block, params.renderType));

        if (params.flammable) FlammableBlockRegistry.getDefaultInstance().add(block, 5, 5);

        for (String tab : params.creativeTabs) {
            ResourceKey<CreativeModeTab> itemGroup = switch (tab) {
                case "BUILDING_BLOCKS" -> CreativeModeTabs.BUILDING_BLOCKS;
                case "COLORED_BLOCKS" -> CreativeModeTabs.COLORED_BLOCKS;
                case "NATURAL_BLOCKS" -> CreativeModeTabs.NATURAL_BLOCKS;
                case "FUNCTIONAL_BLOCKS" -> CreativeModeTabs.FUNCTIONAL_BLOCKS;
                case "REDSTONE_BLOCKS" -> CreativeModeTabs.REDSTONE_BLOCKS;
                case "TOOLS_AND_UTILITIES" -> CreativeModeTabs.TOOLS_AND_UTILITIES;
                case "COMBAT" -> CreativeModeTabs.COMBAT;
                case "FOOD_AND_DRINKS" -> CreativeModeTabs.FOOD_AND_DRINKS;
                case "INGREDIENTS" -> CreativeModeTabs.INGREDIENTS;
                case "SPAWN_EGGS" -> CreativeModeTabs.SPAWN_EGGS;
                default -> CreativeModeTabs.OP_BLOCKS;
            };
            ItemGroupEvents.modifyEntriesEvent(itemGroup).register(content -> content.addAfter(Items.AIR, item));
        }

        return () -> block;
    }

    public static class BlockParams {
        public final Block block;
        public final String renderType;

        BlockParams(Block block, String renderType) {
            this.block = block;
            this.renderType = renderType;
        }
    }
}
