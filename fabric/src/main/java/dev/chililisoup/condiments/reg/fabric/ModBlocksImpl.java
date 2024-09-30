package dev.chililisoup.condiments.reg.fabric;

import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.fabric.CondimentsFabric;
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
        ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(Condiments.MOD_ID, params.id);
        Block block = params.blockFactory.get();
        BlockItem item = params.getItem(block);

        Registry.register(BuiltInRegistries.BLOCK, loc, block);
        Registry.register(BuiltInRegistries.ITEM, loc, item);

        BlocksRegistry.add(new BlockParams(block, params.renderType));

        if (params.flammable) FlammableBlockRegistry.getDefaultInstance().add(block, 5, 5);

        for (String tab : params.creativeTabs) {
            ResourceKey<CreativeModeTab> itemGroup = CondimentsFabric.getCreativeTab(tab);
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
