package dev.chililisoup.condiments.forge;

import com.mojang.datafixers.util.Pair;
import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.client.renderer.CrateRenderer;
import dev.chililisoup.condiments.item.Tooltip.ClientCrateTooltip;
import dev.chililisoup.condiments.item.Tooltip.CrateTooltip;
import dev.chililisoup.condiments.reg.ModBlockEntities;
import dev.chililisoup.condiments.reg.ModBlocks;
import dev.chililisoup.condiments.reg.ModDispenserBehaviors;
import dev.chililisoup.condiments.reg.ModItems;
import dev.chililisoup.condiments.reg.forge.ModBlockEntitiesImpl;
import dev.chililisoup.condiments.reg.forge.ModRecipeSerializersImpl;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Arrays;

import static dev.chililisoup.condiments.reg.forge.ModColorProvidersImpl.BLOCK_COLORS;

@Mod(Condiments.MOD_ID)
public class CondimentsForge {
    private final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Condiments.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Condiments.MOD_ID);

    public static final ArrayList<Pair<RegistryObject<Block>, ModBlocks.Params>> BLOCKS_REGISTRY = new ArrayList<>();
    public static final ArrayList<Pair<RegistryObject<Item>, String[]>> ITEMS_REGISTRY = new ArrayList<>();

    public CondimentsForge() {
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

    public static RegistryObject<Item> registerItem(ModItems.Params params) {
        RegistryObject<Item> item = ITEMS.register(params.id, params.itemFactory);
        ITEMS_REGISTRY.add(new Pair<>(item, params.creativeTabs));
        return item;
    }

    public static RegistryObject<Block> registerBlock(ModBlocks.Params params) {
        RegistryObject<Block> block = BLOCKS.register(params.id, params.blockFactory);
        registerItem(params.getItemParams(block));
        BLOCKS_REGISTRY.add(new Pair<>(block, params));
        return block;
    }

    private void addContents(BuildCreativeModeTabContentsEvent event, String tab) {
        ITEMS_REGISTRY.forEach(item -> {
            if (Arrays.asList(item.getSecond()).contains(tab)) event.accept(item.getFirst());
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