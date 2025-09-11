//? if >= 1.21 {
package dev.chililisoup.condiments.dynamicpack;

import com.mojang.blaze3d.platform.NativeImage;
import dev.chililisoup.condiments.Condiments;
import net.mehvahdjukaar.moonlight.api.events.AfterLanguageLoadEvent;
import net.mehvahdjukaar.moonlight.api.item.WoodBasedBlockItem;
import net.mehvahdjukaar.moonlight.api.resources.RPUtils;
import net.mehvahdjukaar.moonlight.api.resources.ResType;
import net.mehvahdjukaar.moonlight.api.resources.StaticResource;
import net.mehvahdjukaar.moonlight.api.resources.assets.LangBuilder;
import net.mehvahdjukaar.moonlight.api.resources.pack.*;
import net.mehvahdjukaar.moonlight.api.resources.textures.Palette;
import net.mehvahdjukaar.moonlight.api.resources.textures.PaletteColor;
import net.mehvahdjukaar.moonlight.api.resources.textures.Respriter;
import net.mehvahdjukaar.moonlight.api.resources.textures.TextureImage;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static dev.chililisoup.condiments.reg.ModBlockSetVariants.*;

//$ client_only
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class ClientDynamicResourcesGenerator extends DynamicClientResourceProvider {
    private static ClientDynamicResourcesGenerator INSTANCE;

    public static ClientDynamicResourcesGenerator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClientDynamicResourcesGenerator();
        }
        return INSTANCE;
    }

    public ClientDynamicResourcesGenerator() {
        super(Condiments.loc("dynamic_resources"), PackGenerationStrategy.CACHED_ZIPPED);
    }

    @Override
    protected Collection<String> gatherSupportedNamespaces() {
        return List.of("minecraft");
    }

    @Override
    public void regenerateDynamicAssets(Consumer<ResourceGenTask> executor) {
        executor.accept(this::buildAssets);
    }

    private void buildAssets(ResourceManager manager, ResourceSink sink) {
        addWoodWallAssets(manager, sink);
        addWoodAccentAssets(manager, sink);
        addPolishedWoodAssets(manager, sink);
    }

    private void addWoodWallAssets(ResourceManager manager, ResourceSink sink) {
        StaticResource itemModel = StaticResource.getOrLog(manager, ResType.ITEM_MODELS.getPath(Condiments.loc("oak_wall")));

        StaticResource blockState = StaticResource.getOrLog(manager, ResType.BLOCKSTATES.getPath(Condiments.loc("oak_wall")));

        StaticResource post = StaticResource.getOrLog(manager, ResType.BLOCK_MODELS.getPath(Condiments.loc("wood_walls/oak_wall_post")));
        StaticResource sideTallX = StaticResource.getOrLog(manager, ResType.BLOCK_MODELS.getPath(Condiments.loc("wood_walls/oak_wall_side_tall_x")));
        StaticResource sideTallZ = StaticResource.getOrLog(manager, ResType.BLOCK_MODELS.getPath(Condiments.loc("wood_walls/oak_wall_side_tall_z")));
        StaticResource sideX = StaticResource.getOrLog(manager, ResType.BLOCK_MODELS.getPath(Condiments.loc("wood_walls/oak_wall_side_x")));
        StaticResource sideZ = StaticResource.getOrLog(manager, ResType.BLOCK_MODELS.getPath(Condiments.loc("wood_walls/oak_wall_side_z")));

        WOOD_WALLS.items.forEach((wood, wall) -> {
            try {
                String id = Utils.getID(wall).getPath();

                ResourceLocation log = RPUtils.findFirstBlockTextureLocation(
                        manager, wood.getBlockOfThis("log"), t -> !t.contains("top")
                );

                ResourceLocation strippedLog = RPUtils.findFirstBlockTextureLocation(
                        manager, wood.getBlockOfThis("stripped_log"), t -> !t.contains("top")
                );

                Function<String, String> textTransform = s -> s
                        .replace("oak_wall", id)
                        .replace("minecraft:block/oak_log", log.toString())
                        .replace("minecraft:block/stripped_oak_log", strippedLog.toString());

                sink.addSimilarJsonResource(manager, itemModel, textTransform);

                sink.addSimilarJsonResource(manager, blockState, "oak_wall", id);

                sink.addSimilarJsonResource(manager, post, textTransform);
                sink.addSimilarJsonResource(manager, sideTallX, textTransform);
                sink.addSimilarJsonResource(manager, sideTallZ, textTransform);
                sink.addSimilarJsonResource(manager, sideX, textTransform);
                sink.addSimilarJsonResource(manager, sideZ, textTransform);
            } catch (Exception e) {
                Condiments.LOGGER.error("Failed to create log model for {}", wall, e);
            }
        });
    }

    private void addWoodAccentAssets(ResourceManager manager, ResourceSink sink) {
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
                    sink.addTexture(
                            textureLoc,
                            TextureImage.of(buildAccentTexture(top.getImage(), side.getImage()))
                    );
                }
            } catch (Exception e) {
                Condiments.LOGGER.error("Failed to generate wood accent assets for {} : {}", accent, e);
            }
        });
    }

    private void addPolishedWoodAssets(ResourceManager manager, ResourceSink sink) {
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
                        try (TextureImage newImage = topRespriter.recolor(palette)) {
                            sink.addTexture(
                                    textureLoc.withSuffix("_top"),
                                    newImage
                            );
                        }

                        try (TextureImage newImage = sideRespriter.recolor(palette)) {
                            sink.addTexture(
                                    textureLoc,
                                    newImage
                            );
                        }
                    }
                } catch (Exception e) {
                    Condiments.LOGGER.error("Failed to generate polished wood assets for {} : {}", logItem, e);
                }
            });
        } catch (Exception e) {
            Condiments.LOGGER.error("Failed to get base polished wood textures", e);
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

    @Override
    protected void addDynamicTranslations(AfterLanguageLoadEvent event) {
        LangBuilder langBuilder = new LangBuilder();

        addWoodWallLang(langBuilder, event::getEntry);
        addWoodAccentLang(langBuilder, event::getEntry);
        addPolishedWoodLang(langBuilder, event::getEntry);

        event.addEntries(langBuilder);
    }

    private void addWoodWallLang(LangBuilder langBuilder, Function<String, String> entryProvider) {
        String template = entryProvider.apply("condiments.translation_template.wood_wall");

        WOOD_WALLS.items.forEach((wood, wall) ->
            langBuilder.addEntry(
                    wall.getBlock(),
                    String.format(template, entryProvider.apply(wood.getTranslationKey()))
            )
        );
    }

    private void addWoodAccentLang(LangBuilder langBuilder, Function<String, String> entryProvider) {
        String template = entryProvider.apply("condiments.translation_template.wood_accent");

        WOOD_ACCENTS.items.forEach((wood, accent) ->
            langBuilder.addEntry(
                    accent.getBlock(),
                    String.format(template, entryProvider.apply(wood.getTranslationKey()))
            )
        );
    }

    private void addPolishedWoodLang(LangBuilder langBuilder, Function<String, String> entryProvider) {
        String template = entryProvider.apply("condiments.translation_template.polished_wood");

        POLISHED_LOGS.items.forEach((wood, logItem) -> {
            WoodBasedBlockItem woodItem = POLISHED_WOOD.items.get(wood);

            langBuilder.addEntry(
                    logItem.getBlock(),
                    String.format(template, entryProvider.apply(Utils.getID(wood.log).toLanguageKey("block")))
            );

            Optional.ofNullable(wood.getBlockOfThis("wood")).ifPresent(block ->
                langBuilder.addEntry(
                        woodItem.getBlock(),
                        String.format(template, entryProvider.apply(Utils.getID(block).toLanguageKey("block")))
                )
            );
        });
    }
}
//?}