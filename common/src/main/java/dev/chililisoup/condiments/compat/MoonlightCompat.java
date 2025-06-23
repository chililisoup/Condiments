package dev.chililisoup.condiments.compat;

import com.mojang.blaze3d.platform.NativeImage;
import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.block.AccentBlock;
import dev.chililisoup.condiments.reg.ModBlockTags;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mehvahdjukaar.moonlight.api.item.WoodBasedBlockItem;
import net.mehvahdjukaar.moonlight.api.misc.Registrator;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.resources.RPUtils;
import net.mehvahdjukaar.moonlight.api.resources.ResType;
import net.mehvahdjukaar.moonlight.api.resources.SimpleTagBuilder;
import net.mehvahdjukaar.moonlight.api.resources.StaticResource;
import net.mehvahdjukaar.moonlight.api.resources.assets.LangBuilder;
import net.mehvahdjukaar.moonlight.api.resources.pack.*;
import net.mehvahdjukaar.moonlight.api.resources.textures.Palette;
import net.mehvahdjukaar.moonlight.api.resources.textures.PaletteColor;
import net.mehvahdjukaar.moonlight.api.resources.textures.Respriter;
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage;
import net.mehvahdjukaar.moonlight.api.set.BlockSetAPI;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MoonlightCompat {
    private static final ArrayList<WoodVariant> WOOD_VARIANTS = new ArrayList<>();

    private static final WoodVariant WOOD_WALLS = new WoodVariant(
            "wall",
            "fence",
            properties -> new WallBlock(properties.forceSolidOn())
    ).setRequiresSolid("stripped_log");

    private static final WoodVariant WOOD_ACCENTS = new WoodVariant(
            "accent",
            "fence",
            AccentBlock::new
    );

    private static final WoodVariant POLISHED_LOGS = new WoodVariant(
            "polished_log",
            "stripped_log",
            RotatedPillarBlock::new,
            new String[]{"stripped_wood"}
    ).setIdGetter(wood -> {
        Block block = wood.getBlockOfThis("stripped_log");
        return block == null ? wood.getVariantId("polished_%s_log") :
                (wood.isVanilla() ? "" : wood.getNamespace() + "/") +
                        Utils.getID(block).getPath().replace("stripped", "polished");
    }).setRequiresSolid();

    private static final WoodVariant POLISHED_WOOD = new WoodVariant(
            "polished_wood",
            "stripped_wood",
            RotatedPillarBlock::new
    ).setIdGetter(wood -> {
        Block block = wood.getBlockOfThis("stripped_wood");
        return block == null ? wood.getVariantId("polished_%s_wood") :
                (wood.isVanilla() ? "" : wood.getNamespace() + "/") +
                        Utils.getID(block).getPath().replace("stripped", "polished");
    }).setRequiresSolid();

    @ExpectPlatform
    private static void markFlammable(Block block) {
        throw new AssertionError();
    }

    public static void init() {
        BlockSetAPI.addDynamicBlockRegistration(MoonlightCompat::registerWoodBlocks, WoodType.class);
        BlockSetAPI.addDynamicItemRegistration(MoonlightCompat::registerWoodItems, WoodType.class);
        RegHelper.addItemsToTabsRegistration(MoonlightCompat::registerItemsToTabs);
        ServerDynamicResourcesGenerator.INSTANCE.register();
    }

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        ClientDynamicResourcesGenerator.INSTANCE.register();
    }

    private static void registerItemsToTabs(RegHelper.ItemToTabEvent event) {
        WOOD_WALLS.items.forEach((wood, wall) -> event.addAfter(
                CreativeModeTabs.BUILDING_BLOCKS,
                itemStack -> itemStack.is(wood.getItemOfThis("slab")),
                wall
        ));

        WOOD_ACCENTS.items.forEach((wood, accent) -> event.addAfter(
                CreativeModeTabs.BUILDING_BLOCKS,
                itemStack -> itemStack.is(wood.getItemOfThis("slab")),
                accent
        ));

        POLISHED_WOOD.items.forEach((wood, accent) -> event.addAfter(
                CreativeModeTabs.BUILDING_BLOCKS,
                itemStack -> itemStack.is(wood.getItemOfThis("stripped_wood")),
                accent
        ));

        POLISHED_LOGS.items.forEach((wood, accent) -> event.addAfter(
                CreativeModeTabs.BUILDING_BLOCKS,
                itemStack -> itemStack.is(wood.getItemOfThis("stripped_wood")),
                accent
        ));
    }

    private static void registerWoodBlocks(Registrator<Block> event, Collection<WoodType> woodTypes) {
        for (WoodVariant woodVariant : WOOD_VARIANTS) {
            for (WoodType wood : woodTypes) {
                if (Arrays.stream(woodVariant.typeRequirements).anyMatch(req -> wood.getBlockOfThis(req) == null))
                    continue;

                Block parent = wood.getBlockOfThis(woodVariant.parent);
                if (parent == null) continue;

                if (woodVariant.requiresSolid != null) {
                    Block solid = wood.getBlockOfThis(woodVariant.requiresSolid);
                    if (solid == null) continue;

                    try {
                        if (!Block.isShapeFullBlock(solid.defaultBlockState().getShape(null, null)))
                            continue;
                    } catch (Exception e) {
                        continue;
                    }
                }

                Block block = woodVariant.blockFactory.apply(BlockBehaviour.Properties.ofFullCopy(parent));
                String name = woodVariant.idGetter.get(wood);

                wood.addChild("condiments:" + woodVariant.name, block);
                event.register(Condiments.loc(name), block);

                if (wood.canBurn()) markFlammable(block);
            }
        }
    }

    private static void registerWoodItems(Registrator<Item> event, Collection<WoodType> woodTypes) {
        for (WoodVariant woodVariant : WOOD_VARIANTS) {
            for (WoodType wood : woodTypes) {
                if (wood.getChild("condiments:" + woodVariant.name) instanceof Block block) {
                    String name = woodVariant.idGetter.get(wood);
                    WoodBasedBlockItem item = new WoodBasedBlockItem(block, new Item.Properties(), wood);

                    event.register(Condiments.loc(name), item);
                    woodVariant.items.put(wood, item);
                }
            }
        }
    }

    private static class WoodVariant {
        public final HashMap<WoodType, WoodBasedBlockItem> items = new HashMap<>();
        public final String name;
        public final String parent;
        public final Function<BlockBehaviour.Properties, ? extends Block> blockFactory;
        public final String[] typeRequirements;
        public IdGetter idGetter;
        @Nullable public String requiresSolid;

        WoodVariant(String name, String parent, Function<BlockBehaviour.Properties, ? extends Block> blockFactory, String[] typeRequirements) {
            this.name = name;
            this.parent = parent;
            this.blockFactory = blockFactory;
            this.typeRequirements = typeRequirements;

            this.idGetter = wood -> wood.getVariantId("%s_" + this.name);

            WOOD_VARIANTS.add(this);
        }

        WoodVariant(String name, String parent, Function<BlockBehaviour.Properties, ? extends Block> blockFactory) {
            this(name, parent, blockFactory, new String[0]);
        }

        public WoodVariant setIdGetter(IdGetter idGetter) {
            this.idGetter = idGetter;
            return this;
        }

        public WoodVariant setRequiresSolid(@Nullable String variant) {
            this.requiresSolid = variant;
            return this;
        }

        public WoodVariant setRequiresSolid() {
            return this.setRequiresSolid(this.parent);
        }

        private interface IdGetter {
            String get(WoodType woodType);
        }
    }

    @Environment(EnvType.CLIENT)
    private static class ClientDynamicResourcesGenerator extends DynClientResourcesGenerator {
        public static final ClientDynamicResourcesGenerator INSTANCE = new ClientDynamicResourcesGenerator();

        public ClientDynamicResourcesGenerator() {
            super(new DynamicTexturePack(Condiments.loc("generated_pack")));
        }

        @Override
        public Logger getLogger() {
            return Condiments.LOGGER;
        }

        @Override
        public boolean dependsOnLoadedPacks() {
            return true;
        }

        @Override
        public void regenerateDynamicAssets(Consumer<ResourceGenTask> executor) {
            executor.accept(this::buildAssets);
        }

        private void buildAssets(ResourceManager manager, ResourceSink sink) {
            LangBuilder langBuilder = new LangBuilder();

            addWoodWallAssets(manager, sink, langBuilder);
            addWoodAccentAssets(manager, sink, langBuilder);
            addPolishedWoodAssets(manager, sink, langBuilder);

            sink.addLang(Condiments.loc("en_us"), langBuilder);
        }

        private void addWoodWallAssets(ResourceManager manager, ResourceSink sink, LangBuilder langBuilder) {
            StaticResource itemModel = StaticResource.getOrLog(manager, ResType.ITEM_MODELS.getPath(Condiments.loc("oak_wall")));

            StaticResource blockState = StaticResource.getOrLog(manager, ResType.BLOCKSTATES.getPath(Condiments.loc("oak_wall")));

            StaticResource post = StaticResource.getOrLog(manager, ResType.BLOCK_MODELS.getPath(Condiments.loc("wood_walls/oak_wall_post")));
            StaticResource sideTallX = StaticResource.getOrLog(manager, ResType.BLOCK_MODELS.getPath(Condiments.loc("wood_walls/oak_wall_side_tall_x")));
            StaticResource sideTallZ = StaticResource.getOrLog(manager, ResType.BLOCK_MODELS.getPath(Condiments.loc("wood_walls/oak_wall_side_tall_z")));
            StaticResource sideX = StaticResource.getOrLog(manager, ResType.BLOCK_MODELS.getPath(Condiments.loc("wood_walls/oak_wall_side_x")));
            StaticResource sideZ = StaticResource.getOrLog(manager, ResType.BLOCK_MODELS.getPath(Condiments.loc("wood_walls/oak_wall_side_z")));

            WOOD_WALLS.items.forEach((wood, wall) -> {
                String id = Utils.getID(wall).getPath();
                String log = Utils.getID(wood.log).getPath();
                String namespace = wood.getNamespace();

                Function<String, String> textTransform = s -> s
                        .replace("oak_wall", id)
                        .replace("oak_log", log)
                        .replace("minecraft", namespace);

                try {
                    sink.addSimilarJsonResource(manager, itemModel, textTransform);

                    sink.addSimilarJsonResource(manager, blockState, "oak_wall", id);

                    sink.addSimilarJsonResource(manager, post, textTransform);
                    sink.addSimilarJsonResource(manager, sideTallX, textTransform);
                    sink.addSimilarJsonResource(manager, sideTallZ, textTransform);
                    sink.addSimilarJsonResource(manager, sideX, textTransform);
                    sink.addSimilarJsonResource(manager, sideZ, textTransform);

                    langBuilder.addEntry(
                            wall.getBlock(),
                            wood.getReadableName() + " Wall"
                    );
                } catch (Exception e) {
                    getLogger().error("Failed to generate wood wall assets for {} : {}", wall, e);
                }
            });
        }

        private void addWoodAccentAssets(ResourceManager manager, ResourceSink sink, LangBuilder langBuilder) {
            StaticResource itemModel = StaticResource.getOrLog(manager, ResType.ITEM_MODELS.getPath(Condiments.loc("oak_accent")));

            StaticResource blockState = StaticResource.getOrLog(manager, ResType.BLOCKSTATES.getPath(Condiments.loc("oak_accent")));

            StaticResource base = StaticResource.getOrLog(manager, ResType.BLOCK_MODELS.getPath(Condiments.loc("accents/oak_accent")));
            StaticResource inner = StaticResource.getOrLog(manager, ResType.BLOCK_MODELS.getPath(Condiments.loc("accents/oak_accent_inner")));
            StaticResource outer = StaticResource.getOrLog(manager, ResType.BLOCK_MODELS.getPath(Condiments.loc("accents/oak_accent_outer")));

            WOOD_ACCENTS.items.forEach((wood, accent) -> {
                String id = Utils.getID(accent).getPath();

                Function<String, String> textTransform = s -> s
                        .replace("oak_accent", id);

                try (
                        TextureImage top = TextureImage.open(
                                manager,
                                RPUtils.findFirstBlockTextureLocation(manager, wood.getBlockOfThis("stripped_log"), t -> t.contains("top"))
                        );
                        TextureImage side = TextureImage.open(
                                manager,
                                RPUtils.findFirstBlockTextureLocation(manager, wood.getBlockOfThis("stripped_log"), t -> !t.contains("top"))
                        )
                ) {
                    sink.addSimilarJsonResource(manager, itemModel, textTransform);

                    sink.addSimilarJsonResource(manager, blockState, textTransform);

                    sink.addSimilarJsonResource(manager, base, textTransform);
                    sink.addSimilarJsonResource(manager, inner, textTransform);
                    sink.addSimilarJsonResource(manager, outer, textTransform);

                    ResourceLocation textureLoc = Condiments.loc("block/" + wood.getTexturePath() + "_accent");
                    if (!sink.alreadyHasTextureAtLocation(manager, textureLoc)) {
                        sink.addAndCloseTexture(
                                textureLoc,
                                TextureImage.of(buildAccentTexture(top.getImage(), side.getImage()))
                        );
                    }

                    langBuilder.addEntry(
                            accent.getBlock(),
                            wood.getReadableName() + " Accent"
                    );
                } catch (Exception e) {
                    getLogger().error("Failed to generate wood accent assets for {} : {}", accent, e);
                }
            });
        }

        private void addPolishedWoodAssets(ResourceManager manager, ResourceSink sink, LangBuilder langBuilder) {
            StaticResource logModel = StaticResource.getOrLog(manager, ResType.ITEM_MODELS.getPath(Condiments.loc("polished_oak_log")));
            StaticResource woodModel = StaticResource.getOrLog(manager, ResType.ITEM_MODELS.getPath(Condiments.loc("polished_oak_wood")));

            StaticResource logState = StaticResource.getOrLog(manager, ResType.BLOCKSTATES.getPath(Condiments.loc("polished_oak_log")));
            StaticResource woodState = StaticResource.getOrLog(manager, ResType.BLOCKSTATES.getPath(Condiments.loc("polished_oak_wood")));

            StaticResource logX = StaticResource.getOrLog(manager, ResType.BLOCK_MODELS.getPath(Condiments.loc("polished_logs/polished_oak_log_x")));
            StaticResource logY = StaticResource.getOrLog(manager, ResType.BLOCK_MODELS.getPath(Condiments.loc("polished_logs/polished_oak_log_y")));
            StaticResource logZ = StaticResource.getOrLog(manager, ResType.BLOCK_MODELS.getPath(Condiments.loc("polished_logs/polished_oak_log_z")));

            StaticResource woodX = StaticResource.getOrLog(manager, ResType.BLOCK_MODELS.getPath(Condiments.loc("polished_logs/polished_oak_wood_x")));
            StaticResource woodY = StaticResource.getOrLog(manager, ResType.BLOCK_MODELS.getPath(Condiments.loc("polished_logs/polished_oak_wood_y")));
            StaticResource woodZ = StaticResource.getOrLog(manager, ResType.BLOCK_MODELS.getPath(Condiments.loc("polished_logs/polished_oak_wood_z")));

            try (
                    TextureImage baseTop = TextureImage.open(
                            manager,
                            Condiments.loc("block/polished_oak_log_top")
                    );
                    TextureImage baseSide = TextureImage.open(
                            manager,
                            Condiments.loc("block/polished_oak_log")
                    )
            ) {
                Respriter topRespriter = Respriter.of(baseTop);
                Respriter sideRespriter = Respriter.of(baseSide);

                POLISHED_LOGS.items.forEach((wood, logItem) -> {
                    WoodBasedBlockItem woodItem = POLISHED_WOOD.items.get(wood);

                    String logId = Utils.getID(logItem).getPath();
                    String woodId = Utils.getID(woodItem).getPath();

                    Function<String, String> textTransform = s -> s
                            .replace("polished_oak_log", logId)
                            .replace("polished_oak_wood", woodId);

                    try (
                            TextureImage top = TextureImage.open(
                                    manager,
                                    RPUtils.findFirstBlockTextureLocation(manager, wood.getBlockOfThis("stripped_log"), t -> t.contains("top"))
                            );
                            TextureImage side = TextureImage.open(
                                    manager,
                                    RPUtils.findFirstBlockTextureLocation(manager, wood.getBlockOfThis("stripped_log"), t -> !t.contains("top"))
                            )
                    ) {
                        sink.addSimilarJsonResource(manager, logModel, textTransform);
                        sink.addSimilarJsonResource(manager, woodModel, textTransform);

                        sink.addSimilarJsonResource(manager, logState, textTransform);
                        sink.addSimilarJsonResource(manager, woodState, textTransform);

                        sink.addSimilarJsonResource(manager, logX, textTransform);
                        sink.addSimilarJsonResource(manager, logY, textTransform);
                        sink.addSimilarJsonResource(manager, logZ, textTransform);

                        sink.addSimilarJsonResource(manager, woodX, textTransform);
                        sink.addSimilarJsonResource(manager, woodY, textTransform);
                        sink.addSimilarJsonResource(manager, woodZ, textTransform);

                        Palette palette = Palette.merge(Palette.fromImage(top), Palette.fromImage(side));

                        PaletteColor lightest = palette.getLightest();
                        PaletteColor darkest = palette.getDarkest();

                        while (palette.size() > 6) palette.reduce();

                        palette.add(lightest);
                        palette.add(darkest);

                        ResourceLocation textureLoc = Condiments.loc("block/" + logId);
                        if (!sink.alreadyHasTextureAtLocation(manager, textureLoc)) {
                            sink.addAndCloseTexture(
                                    textureLoc.withSuffix("_top"),
                                    topRespriter.recolor(palette)
                            );

                            sink.addAndCloseTexture(
                                    textureLoc,
                                    sideRespriter.recolor(palette)
                            );
                        }

                        langBuilder.addEntry(
                                logItem.getBlock(),
                                LangBuilder.getReadableName(
                                        Utils.getID(wood.getBlockOfThis("stripped_log")).getPath().replace("stripped", "polished")
                                )
                        );

                        langBuilder.addEntry(
                                woodItem.getBlock(),
                                LangBuilder.getReadableName(
                                        Utils.getID(wood.getBlockOfThis("stripped_wood")).getPath().replace("stripped", "polished")
                                )
                        );
                    } catch (Exception e) {
                        getLogger().error("Failed to generate polished wood assets for {} : {}", logItem, e);
                    }
                });
            } catch (Exception e) {
                getLogger().error("Failed to get base polished wood textures", e);
            }
        }

        private static @NotNull NativeImage buildAccentTexture(NativeImage top, NativeImage side) {
            if (side.getWidth() < 16) return side;

            int scale = side.getWidth() / 16;

            NativeImage image = new NativeImage(16 * scale, 16 * scale, false);

            copyScaledRect(scale, side, image, 4, 0, 0, 0, 8, 16, false, false);
            copyScaledRect(scale, side, image, 4, 0, 8, 0, 8, 16, false, true);

            copyScaledRect(scale, top, image, 7, 7, 5, 5, 3, 3, true, true);
            copyScaledRect(scale, top, image, 7, 7, 8, 5, 3, 3, true, true);
            copyScaledRect(scale, top, image, 7, 7, 5, 8, 3, 3, true, true);
            copyScaledRect(scale, top, image, 7, 7, 8, 8, 3, 3, true, true);

            return image;
        }

        private static void copyScaledRect(
                int scale, NativeImage from, NativeImage to, int xFrom, int yFrom, int xTo, int yTo, int width, int height, boolean mirrorX, boolean mirrorY
        ) {
            from.copyRect(to, xFrom, yFrom * scale, xTo * scale, yTo * scale, width * scale, height * scale, mirrorX, mirrorY);
        }
    }

    // Doing it the non-deprecated way is buggy at the moment
    @SuppressWarnings("removal")
    private static class ServerDynamicResourcesGenerator extends DynServerResourcesGenerator {
        public static final ServerDynamicResourcesGenerator INSTANCE = new ServerDynamicResourcesGenerator();

        public ServerDynamicResourcesGenerator() {
            super(new DynamicDataPack(Condiments.loc("generated_pack")));
        }

        @Override
        public Logger getLogger() {
            return Condiments.LOGGER;
        }

        @Override
        public boolean dependsOnLoadedPacks() {
            return true;
        }

        @Override
        public Collection<String> additionalNamespaces() {
            Condiments.LOGGER.info(PlatHelper.getInstalledMods().toString());

            return PlatHelper.getInstalledMods();
        }

        @Override
        public void regenerateDynamicAssets(Consumer<ResourceGenTask> executor) {
            executor.accept((manager, sink) -> {
                addWoodWallData(manager, sink);
                addWoodAccentData(manager, sink);
                addPolishedWoodData(manager, sink);
            });
        }

        private void addWoodWallData(ResourceManager manager, ResourceSink sink) {
            SimpleTagBuilder builder = SimpleTagBuilder.of(ModBlockTags.WOOD_WALLS);
            builder.addEntries(WOOD_WALLS.items.values().stream().map(BlockItem::getBlock).collect(Collectors.toSet()));
            dynamicPack.addTag(builder, Registries.BLOCK);

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
            SimpleTagBuilder builder = SimpleTagBuilder.of(Condiments.loc("wood_accents"));
            builder.addEntries(WOOD_ACCENTS.items.values().stream().map(BlockItem::getBlock).collect(Collectors.toSet()));
            dynamicPack.addTag(builder, Registries.BLOCK);

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
            SimpleTagBuilder polishedTagBuilder = SimpleTagBuilder.of(Condiments.loc("polished_logs"));

            StaticResource logLootTable = StaticResource.getOrLog(manager, ResType.LOOT_TABLES.getPath(Condiments.loc("blocks/polished_oak_log")));
            StaticResource woodLootTable = StaticResource.getOrLog(manager, ResType.LOOT_TABLES.getPath(Condiments.loc("blocks/polished_oak_wood")));

            StaticResource logRecipe = StaticResource.getOrLog(manager, ResType.RECIPES.getPath(Condiments.loc("polished_wood/polished_oak_log")));
            StaticResource logsToWoodRecipe = StaticResource.getOrLog(manager, ResType.RECIPES.getPath(Condiments.loc("polished_wood/polished_oak_logs_to_wood")));
            StaticResource woodRecipe = StaticResource.getOrLog(manager, ResType.RECIPES.getPath(Condiments.loc("polished_wood/polished_oak_wood")));

            StaticResource logAdvancement = StaticResource.getOrLog(manager, ResType.ADVANCEMENTS.getPath(Condiments.loc("recipes/polished_wood/polished_oak_log")));
            StaticResource woodAdvancement = StaticResource.getOrLog(manager, ResType.ADVANCEMENTS.getPath(Condiments.loc("recipes/polished_wood/polished_oak_wood")));

            POLISHED_LOGS.items.forEach((wood, logItem) -> {
                WoodBasedBlockItem woodItem = POLISHED_WOOD.items.get(wood);

                polishedTagBuilder.addEntry(logItem.getBlock());
                polishedTagBuilder.addEntry(woodItem.getBlock());

                ResourceLocation loc = ResourceLocation.fromNamespaceAndPath(
                        wood.getNamespace(),
                        Utils.getID(wood.log).getPath() + "s"
                );

                SimpleTagBuilder blockTagBuilder = SimpleTagBuilder.of(loc);
                blockTagBuilder.addEntry(logItem.getBlock());
                blockTagBuilder.addEntry(woodItem.getBlock());
                dynamicPack.addTag(blockTagBuilder, Registries.BLOCK);

                SimpleTagBuilder itemTagBuilder = SimpleTagBuilder.of(loc);
                itemTagBuilder.addEntry(logItem);
                itemTagBuilder.addEntry(woodItem);
                dynamicPack.addTag(itemTagBuilder, Registries.ITEM);


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

            dynamicPack.addTag(polishedTagBuilder, Registries.BLOCK);
        }
    }
}
