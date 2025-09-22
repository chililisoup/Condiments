package dev.chililisoup.condiments.dynamicpack;

import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.reg.ModBlockTags;
import net.mehvahdjukaar.moonlight.api.item.WoodBasedBlockItem;
import net.mehvahdjukaar.moonlight.api.resources.ResType;
import net.mehvahdjukaar.moonlight.api.resources.SimpleTagBuilder;
import net.mehvahdjukaar.moonlight.api.resources.StaticResource;
import net.mehvahdjukaar.moonlight.api.resources.pack.*;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.BlockItem;

//? if < 1.21 {
/*import org.apache.logging.log4j.Logger;
*///?} else {
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import java.util.Collection;
//?}

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static dev.chililisoup.condiments.reg.ModBlockSetVariants.*;

public class ServerDynamicResourcesGenerator extends
        //? if < 1.21 {
        /*DynServerResourcesGenerator
        *///?} else
        DynamicServerResourceProvider
{
    //? if < 1.21
    /*public static final ServerDynamicResourcesGenerator INSTANCE = new ServerDynamicResourcesGenerator();*/

    public ServerDynamicResourcesGenerator() {
        //? if < 1.21 {
        /*super(new DynamicDataPack(Condiments.loc("generated_pack")));
        *///?} else
        super(Condiments.loc("dynamic_resources"), PackGenerationStrategy.CACHED_ZIPPED);
    }

    //? if < 1.21 {
    /*@Override
    public Logger getLogger() {
        return Condiments.LOGGER;
    }
    *///?} else {
    @Override
    protected Collection<String> gatherSupportedNamespaces() {
        return PlatHelper.getInstalledMods();
    }
    //?}

    @Override
    public void regenerateDynamicAssets(Consumer<ResourceGenTask> executor) {
        executor.accept((manager, sink) -> {
            addWoodWallData(manager, sink);
            addWoodAccentData(manager, sink);
            addPolishedWoodData(manager, sink);
        });
    }

    private void addWoodWallData(ResourceManager manager, ResourceSink sink) {
        SimpleTagBuilder blockTagBuilder = SimpleTagBuilder.of(ModBlockTags.WOOD_WALLS);
        blockTagBuilder.addEntries(WOOD_WALLS.items.values().stream().map(BlockItem::getBlock).collect(Collectors.toSet()));
        sink.addTag(blockTagBuilder, Registries.BLOCK);

        SimpleTagBuilder itemTagBuilder = SimpleTagBuilder.of(ModBlockTags.WOOD_WALLS);
        itemTagBuilder.addEntries(WOOD_WALLS.items.values());
        sink.addTag(itemTagBuilder, Registries.ITEM);

        StaticResource lootTable = StaticResource.getOrLog(manager, ResType.LOOT_TABLES.getPath(Condiments.loc("blocks/oak_wall")));
        StaticResource recipe = StaticResource.getOrLog(manager, ResType.RECIPES.getPath(Condiments.loc("wood_walls/oak_wall")));
        StaticResource advancement = StaticResource.getOrLog(manager, ResType.ADVANCEMENTS.getPath(Condiments.loc("recipes/wood_walls/oak_wall")));

        WOOD_WALLS.items.forEach((wood, wall) -> {
            String log = Utils.getID(wood.log).toString();
            String id = Utils.getID(wall).getPath();

            Function<String, String> textTransform = s -> s
                    .replace("minecraft:oak_log", log)
                    .replace("oak_wall", id);

            sink.addSimilarJsonResource(manager, lootTable, textTransform);
            sink.addSimilarJsonResource(manager, recipe, textTransform);
            sink.addSimilarJsonResource(manager, advancement, textTransform);
        });
    }

    private void addWoodAccentData(ResourceManager manager, ResourceSink sink) {
        SimpleTagBuilder blockTagBuilder = SimpleTagBuilder.of(Condiments.loc("wood_accents"));
        blockTagBuilder.addEntries(WOOD_ACCENTS.items.values().stream().map(BlockItem::getBlock).collect(Collectors.toSet()));
        sink.addTag(blockTagBuilder, Registries.BLOCK);

        SimpleTagBuilder itemTagBuilder = SimpleTagBuilder.of(Condiments.loc("wood_accents"));
        itemTagBuilder.addEntries(WOOD_ACCENTS.items.values());
        sink.addTag(itemTagBuilder, Registries.ITEM);

        StaticResource lootTable = StaticResource.getOrLog(manager, ResType.LOOT_TABLES.getPath(Condiments.loc("blocks/oak_accent")));
        StaticResource recipe = StaticResource.getOrLog(manager, ResType.RECIPES.getPath(Condiments.loc("accents/oak_accent")));
        StaticResource advancement = StaticResource.getOrLog(manager, ResType.ADVANCEMENTS.getPath(Condiments.loc("recipes/accents/oak_accent")));

        WOOD_ACCENTS.items.forEach((wood, accent) -> {
            String log = Utils.getID(wood.getBlockOfThis("stripped_log")).toString();
            String id = Utils.getID(accent).getPath();

            Function<String, String> textTransform = s -> s
                    .replace("minecraft:stripped_oak_log", log)
                    .replace("oak_accent", id);

            sink.addSimilarJsonResource(manager, lootTable, textTransform);
            sink.addSimilarJsonResource(manager, recipe, textTransform);
            sink.addSimilarJsonResource(manager, advancement, textTransform);
        });
    }

    private void addPolishedWoodData(ResourceManager manager, ResourceSink sink) {
        SimpleTagBuilder polishedBlockTagBuilder = SimpleTagBuilder.of(Condiments.loc("polished_logs"));
        SimpleTagBuilder polishedItemTagBuilder = SimpleTagBuilder.of(Condiments.loc("polished_logs"));

        StaticResource logLootTable = StaticResource.getOrLog(manager, ResType.LOOT_TABLES.getPath(Condiments.loc("blocks/polished_oak_log")));
        StaticResource woodLootTable = StaticResource.getOrLog(manager, ResType.LOOT_TABLES.getPath(Condiments.loc("blocks/polished_oak_wood")));

        StaticResource logRecipe = StaticResource.getOrLog(manager, ResType.RECIPES.getPath(Condiments.loc("polished_wood/polished_oak_log")));
        StaticResource logsToWoodRecipe = StaticResource.getOrLog(manager, ResType.RECIPES.getPath(Condiments.loc("polished_wood/polished_oak_logs_to_wood")));
        StaticResource woodRecipe = StaticResource.getOrLog(manager, ResType.RECIPES.getPath(Condiments.loc("polished_wood/polished_oak_wood")));

        StaticResource logAdvancement = StaticResource.getOrLog(manager, ResType.ADVANCEMENTS.getPath(Condiments.loc("recipes/polished_wood/polished_oak_log")));
        StaticResource woodAdvancement = StaticResource.getOrLog(manager, ResType.ADVANCEMENTS.getPath(Condiments.loc("recipes/polished_wood/polished_oak_wood")));

        POLISHED_LOGS.items.forEach((wood, logItem) -> {
            WoodBasedBlockItem woodItem = POLISHED_WOOD.items.get(wood);

            polishedBlockTagBuilder.addEntry(logItem.getBlock());
            polishedBlockTagBuilder.addEntry(woodItem.getBlock());

            polishedItemTagBuilder.addEntry(logItem);
            polishedItemTagBuilder.addEntry(woodItem);

            ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(
                    wood.getNamespace(),
                    Utils.getID(wood.log).getPath() + "s"
            );

            SimpleTagBuilder blockTagBuilder = SimpleTagBuilder.of(loc);
            blockTagBuilder.addEntry(logItem.getBlock());
            blockTagBuilder.addEntry(woodItem.getBlock());
            sink.addTag(blockTagBuilder, Registries.BLOCK);

            SimpleTagBuilder itemTagBuilder = SimpleTagBuilder.of(loc);
            itemTagBuilder.addEntry(logItem);
            itemTagBuilder.addEntry(woodItem);
            sink.addTag(itemTagBuilder, Registries.ITEM);


            String strippedLog = Utils.getID(wood.getBlockOfThis("stripped_log")).toString();
            String strippedWood = Utils.getID(wood.getBlockOfThis("stripped_wood")).toString();
            String logId = Utils.getID(logItem).getPath();
            String woodId = Utils.getID(woodItem).getPath();

            Function<String, String> textTransform = s -> s
                    .replace("minecraft:stripped_oak_log", strippedLog)
                    .replace("minecraft:stripped_oak_wood", strippedWood)
                    .replace("polished_oak_log", logId)
                    .replace("polished_oak_wood", woodId);

            sink.addSimilarJsonResource(manager, logLootTable, textTransform);
            sink.addSimilarJsonResource(manager, woodLootTable, textTransform);

            sink.addSimilarJsonResource(manager, logRecipe, textTransform);
            sink.addSimilarJsonResource(manager, logsToWoodRecipe, textTransform);
            sink.addSimilarJsonResource(manager, woodRecipe, textTransform);

            sink.addSimilarJsonResource(manager, logAdvancement, textTransform);
            sink.addSimilarJsonResource(manager, woodAdvancement, textTransform);
        });

        sink.addTag(polishedBlockTagBuilder, Registries.BLOCK);
        sink.addTag(polishedItemTagBuilder, Registries.ITEM);
    }
}