package dev.chililisoup.condiments.reg;

import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.block.AccentBlock;
import net.mehvahdjukaar.moonlight.api.item.WoodBasedBlockItem;
import net.mehvahdjukaar.moonlight.api.misc.Registrator;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.set.BlockSetAPI;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodTypeRegistry;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class ModBlockSetVariants {
    public static final Map<String, String> WOOD_OVERRIDES = Map.of(
            "mynethersdelight:powdery stripped_log", "mynethersdelight:stripped_powdery_block"
    );

    public static final ArrayList<WoodVariant> WOOD_VARIANTS = new ArrayList<>();

    public static final WoodVariant WOOD_WALLS = new WoodVariant(
            "wall",
            "fence",
            properties -> new WallBlock(properties.forceSolidOn()),
            "log"
    ).setRequiresSolid("stripped_log");

    public static final WoodVariant WOOD_ACCENTS = new WoodVariant(
            "accent",
            "fence",
            AccentBlock::new
    ).setRequiresSolid("stripped_log")
            .setIgnoresSolid("natures_spirit:joshua")
            .blacklist("ecologics:flowering_azalea");

    public static final WoodVariant POLISHED_WOOD = new WoodVariant(
            "polished_wood",
            "stripped_wood",
            RotatedPillarBlock::new,
            "stripped_log", "wood"
    ).setIdGetter(wood -> {
        Block block = getWoodBlock(wood, "wood");
        return block == null ? wood.getVariantId("polished_%s_wood") :
                (wood.isVanilla() ? "" : wood.getNamespace() + "/") +
                        "polished_" + Utils.getID(block).getPath();
    }).setRequiresSolid()
            .blacklist("ecologics:flowering_azalea");

    public static final WoodVariant POLISHED_LOGS = new WoodVariant(
            "polished_log",
            "stripped_log",
            RotatedPillarBlock::new,
            "stripped_wood"
    ).setIdGetter(wood ->
            (wood.isVanilla() ? "" : wood.getNamespace() + "/") +
                    "polished_" + Utils.getID(wood.log).getPath()
    ).setRequiresSolid()
            .blacklist("ecologics:flowering_azalea");

    private static void markFlammable(Block block) {
        RegHelper.registerBlockFlammability(block, 5, 5);
    }

    public static void init() {
        BlockSetAPI.addDynamicRegistration(
                //? if < 1.21 {
                /*ModBlockSetVariants::registerWoodBlocks, WoodType.class, BuiltInRegistries.BLOCK
                *///?} else
                Condiments.MOD_ID, ModBlockSetVariants::registerWoodBlocks, BuiltInRegistries.BLOCK
        );
        BlockSetAPI.addDynamicRegistration(
                //? if < 1.21 {
                /*ModBlockSetVariants::registerWoodItems, WoodType.class, BuiltInRegistries.ITEM
                *///?} else
                Condiments.MOD_ID, ModBlockSetVariants::registerWoodItems, BuiltInRegistries.ITEM
        );
    }

    public static @Nullable Block getWoodBlock(WoodType wood, String variant) {
        Block base = wood.getBlockOfThis(variant);
        if (base != null) return base;

        String override = WOOD_OVERRIDES.get(wood.id.toString() + " " + variant);
        if (override == null) return null;

        ResourceLocation overrideLoc = ResourceLocation.tryParse(override);
        if (overrideLoc == null) return null;

        return BuiltInRegistries.BLOCK.getOptional(overrideLoc).orElse(null);
    }

    private static void registerWoodBlocks(
            //? if < 1.21 {
            /*Registrator<Block> event, Collection<WoodType> woodTypes
            *///?} else
            Registrator<Block> event
    ) {
        for (WoodVariant woodVariant : WOOD_VARIANTS) {
            for (WoodType wood : /*? if < 1.21 {*//*woodTypes*//*?} else {*/WoodTypeRegistry.INSTANCE/*?}*/) {
                if (Arrays.stream(woodVariant.typeRequirements).anyMatch(req -> getWoodBlock(wood, req) == null))
                    continue;

                String id = wood.id.toString();
                if (woodVariant.blacklist.contains(id)) continue;

                Block parent = getWoodBlock(wood, woodVariant.parent);
                if (parent == null) continue;

                if (!woodVariant.ignoresSolid.contains(id)) {
                    if (woodVariant.requiresSolid.stream().anyMatch(variant -> {
                        Block solid = getWoodBlock(wood, variant);
                        if (solid == null) return true;

                        try {
                            if (!Block.isShapeFullBlock(solid.defaultBlockState().getShape(null, null)))
                                return true;
                        } catch (Exception e) {
                            return true;
                        }

                        return false;
                    })) continue;
                }

                Block block = woodVariant.blockFactory.apply(BlockBehaviour.Properties.ofFullCopy(parent));
                String name = woodVariant.idGetter.get(wood);

                wood.addChild("condiments:" + woodVariant.name, block);
                event.register(Condiments.loc(name), block);

                if (wood.canBurn()) markFlammable(block);
            }
        }
    }


    private static void registerWoodItems(
            //? if < 1.21 {
            /*Registrator<Item> event, Collection<WoodType> woodTypes
            *///?} else
            Registrator<Item> event
    ) {
        for (WoodVariant woodVariant : WOOD_VARIANTS) {
            for (WoodType wood : /*? if < 1.21 {*//*woodTypes*//*?} else {*/WoodTypeRegistry.INSTANCE/*?}*/) {
                if (wood.getChild("condiments:" + woodVariant.name) instanceof Block block) {
                    String name = woodVariant.idGetter.get(wood);
                    WoodBasedBlockItem item = new WoodBasedBlockItem(block, new Item.Properties(), wood);

                    event.register(Condiments.loc(name), item);
                    woodVariant.items.put(wood, item);
                }
            }
        }
    }

    public static class WoodVariant {
        public final HashMap<WoodType, WoodBasedBlockItem> items = new HashMap<>();
        public final String name;
        public final String parent;
        public final Function<BlockBehaviour.Properties, ? extends Block> blockFactory;
        public final String[] typeRequirements;
        public IdGetter idGetter;
        public final Set<String> requiresSolid = new HashSet<>();
        public final Set<String> ignoresSolid = new HashSet<>();
        public final Set<String> blacklist = new HashSet<>();

        WoodVariant(String name, String parent, Function<BlockBehaviour.Properties, ? extends Block> blockFactory, String... typeRequirements) {
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

        public WoodVariant setRequiresSolid(String... variants) {
            this.requiresSolid.addAll(List.of(variants));
            return this;
        }

        public WoodVariant setIgnoresSolid(String... ids) {
            this.ignoresSolid.addAll(List.of(ids));
            return this;
        }

        public WoodVariant setRequiresSolid() {
            return this.setRequiresSolid(this.parent);
        }

        public WoodVariant blacklist(String... ids) {
            this.blacklist.addAll(List.of(ids));
            return this;
        }

        public interface IdGetter {
            String get(WoodType woodType);
        }
    }
}
