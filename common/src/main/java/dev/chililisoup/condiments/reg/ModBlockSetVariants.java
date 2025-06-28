package dev.chililisoup.condiments.reg;

import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.block.AccentBlock;
import net.mehvahdjukaar.moonlight.api.item.WoodBasedBlockItem;
import net.mehvahdjukaar.moonlight.api.misc.Registrator;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.set.BlockSetAPI;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.mehvahdjukaar.moonlight.api.util.Utils;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class ModBlockSetVariants {
    public static final ArrayList<WoodVariant> WOOD_VARIANTS = new ArrayList<>();

    public static final WoodVariant WOOD_WALLS = new WoodVariant(
            "wall",
            "fence",
            properties -> new WallBlock(properties.forceSolidOn())
    ).setRequiresSolid("stripped_log");

    public static final WoodVariant WOOD_ACCENTS = new WoodVariant(
            "accent",
            "fence",
            AccentBlock::new
    );

    public static final WoodVariant POLISHED_WOOD = new WoodVariant(
            "polished_wood",
            "stripped_wood",
            RotatedPillarBlock::new
    ).setIdGetter(wood -> {
        Block block = wood.getBlockOfThis("stripped_wood");
        return block == null ? wood.getVariantId("polished_%s_wood") :
                (wood.isVanilla() ? "" : wood.getNamespace() + "/") +
                        Utils.getID(block).getPath().replace("stripped", "polished");
    }).setRequiresSolid();

    public static final WoodVariant POLISHED_LOGS = new WoodVariant(
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

    private static void markFlammable(Block block) {
        RegHelper.registerBlockFlammability(block, 5, 5);
    }

    public static void init() {
        BlockSetAPI.addDynamicBlockRegistration(ModBlockSetVariants::registerWoodBlocks, WoodType.class);
        BlockSetAPI.addDynamicItemRegistration(ModBlockSetVariants::registerWoodItems, WoodType.class);
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

    public static class WoodVariant {
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

        public interface IdGetter {
            String get(WoodType woodType);
        }
    }
}
