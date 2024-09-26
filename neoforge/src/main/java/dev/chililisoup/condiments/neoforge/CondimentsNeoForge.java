package dev.chililisoup.condiments.neoforge;

import com.mojang.datafixers.util.Pair;
import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.client.renderer.CrateRenderer;
import dev.chililisoup.condiments.item.tooltip.ClientCrateTooltip;
import dev.chililisoup.condiments.item.tooltip.CrateTooltip;
import dev.chililisoup.condiments.reg.ModBlockEntities;
import dev.chililisoup.condiments.reg.ModBlocks;
import dev.chililisoup.condiments.reg.ModDispenserBehaviors;
import dev.chililisoup.condiments.reg.ModItems;
import dev.chililisoup.condiments.reg.neoforge.ModBlockEntitiesImpl;
import dev.chililisoup.condiments.reg.neoforge.ModRecipeSerializersImpl;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

import static dev.chililisoup.condiments.reg.neoforge.ModColorProvidersImpl.BLOCK_COLORS;

@Mod(Condiments.MOD_ID)
public class CondimentsNeoForge {
    private final IEventBus eventBus;

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.createBlocks(Condiments.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(Condiments.MOD_ID);

    public static final ArrayList<Pair<DeferredHolder<Block, ? extends Block>, ModBlocks.Params>> BLOCKS_REGISTRY = new ArrayList<>();
    public static final ArrayList<Pair<DeferredHolder<Item, ? extends Item>, String[]>> ITEMS_REGISTRY = new ArrayList<>();

    public CondimentsNeoForge(IEventBus eventBus) {
        this.eventBus = eventBus;

        Condiments.init();

        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);

        eventBus.addListener(this::buildContents);
        eventBus.addListener(this::commonSetup);
        eventBus.addListener(this::constructMod);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            Condiments.initClient();
            eventBus.addListener(this::registerEntityRenderers);
            eventBus.addListener(this::registerClientTooltips);
            eventBus.addListener(this::registerBlockColors);
        }
    }

    public static Supplier<Item> registerItem(ModItems.Params params) {
        DeferredHolder<Item, ? extends Item> item = ITEMS.register(params.id, params.itemFactory);
        ITEMS_REGISTRY.add(new Pair<>(item, params.creativeTabs));
        return item::get;
    }

    public static Supplier<Block> registerBlock(ModBlocks.Params params) {
        DeferredHolder<Block, ? extends Block> block = BLOCKS.register(params.id, params.blockFactory);
        registerItem(params.getItemParams(block));
        BLOCKS_REGISTRY.add(new Pair<>(block, params));
        return block::get;
    }

    private void addContents(BuildCreativeModeTabContentsEvent event, String tab) {
        ITEMS_REGISTRY.forEach(item -> {
            if (Arrays.asList(item.getSecond()).contains(tab)) event.accept(item.getFirst().get());
        });
    }

    public void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) addContents(event, "BUILDING_BLOCKS");
        else if (event.getTabKey() == CreativeModeTabs.COLORED_BLOCKS) addContents(event, "COLORED_BLOCKS");
        else if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) addContents(event, "NATURAL_BLOCKS");
        else if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) addContents(event, "FUNCTIONAL_BLOCKS");
        else if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) addContents(event, "REDSTONE_BLOCKS");
        else if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) addContents(event, "TOOLS_AND_UTILITIES");
        else if (event.getTabKey() == CreativeModeTabs.COMBAT) addContents(event, "COMBAT");
        else if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) addContents(event, "FOOD_AND_DRINKS");
        else if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) addContents(event, "INGREDIENTS");
        else if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) addContents(event, "SPAWN_EGGS");
        else if (event.getTabKey() == CreativeModeTabs.OP_BLOCKS) addContents(event, "OP_BLOCKS");
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        BLOCKS_REGISTRY.forEach(reg -> {
            if (reg.getSecond().flammable) ((FireBlock) Blocks.FIRE).setFlammable(reg.getFirst().get(), 5, 5);
        });

        ModDispenserBehaviors.init();
        //ModWaxingPairs.init();
    }

    public void constructMod(FMLConstructModEvent event) {
        ModBlockEntities.init();
        ModBlockEntitiesImpl.BLOCK_ENTITY_TYPES.register(eventBus);
        ModRecipeSerializersImpl.RECIPE_SERIALIZERS.register(eventBus);
    }

    public void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.CRATE_BE_TYPE.get(), CrateRenderer::new);
    }

    public void registerClientTooltips(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(CrateTooltip.class, ClientCrateTooltip::new);
    }

    public void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        BLOCK_COLORS.forEach(reg -> event.register(reg.getFirst(), reg.getSecond().get()));
    }
}