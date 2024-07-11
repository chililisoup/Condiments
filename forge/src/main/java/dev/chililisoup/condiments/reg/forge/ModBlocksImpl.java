package dev.chililisoup.condiments.reg.forge;

import com.mojang.datafixers.util.Pair;
import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.reg.ModBlocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;

import java.util.ArrayList;
import java.util.function.Supplier;

public class ModBlocksImpl {
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Condiments.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Condiments.MOD_ID);

    public static final ArrayList<BlockParams> BlocksRegistry = new ArrayList<>();
    public static final ArrayList<Pair<RegistryObject<Item>, String[]>> ItemsRegistry = new ArrayList<>();


    public static Supplier<Block> addBlock(ModBlocks.Params params) {
        RegistryObject<Block> block = BLOCKS.register(params.id, params.blockFactory);
        ItemsRegistry.add(new Pair<>(ITEMS.register(params.id, () -> params.getItem(block.get())), params.creativeTabs));
        BlocksRegistry.add(new BlockParams(block, params.flammable));
        return block;
    }

    public static void init() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }

    public static class BlockParams {
        public final RegistryObject<Block> block;
        public final boolean flammable;

        BlockParams(RegistryObject<Block> block, Boolean flammable) {
            this.block = block;
            this.flammable = flammable;
        }
    }
}
