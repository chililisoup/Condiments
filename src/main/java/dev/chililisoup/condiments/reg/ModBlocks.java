package dev.chililisoup.condiments.reg;

import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.block.*;
import dev.chililisoup.condiments.extra.VersionHelper;
import dev.chililisoup.condiments.item.CrateItem;
import dev.chililisoup.condiments.block.entity.CrateContents;
import net.mehvahdjukaar.moonlight.api.misc.RegSupplier;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.HashMap;
import java.util.function.Supplier;

public class ModBlocks {
    public static final HashMap<Supplier<Block>, String> ALT_RENDERED_BLOCKS = new HashMap<>();

    public static final Supplier<Block> RAIL_INTERSECTION;
    public static final Supplier<Block> ANALOG_RAIL;

    public static final Supplier<Block> CRATE;
    public static final Supplier<Block> WHITE_CRATE;
    public static final Supplier<Block> LIGHT_GRAY_CRATE;
    public static final Supplier<Block> GRAY_CRATE;
    public static final Supplier<Block> BLACK_CRATE;
    public static final Supplier<Block> BROWN_CRATE;
    public static final Supplier<Block> RED_CRATE;
    public static final Supplier<Block> ORANGE_CRATE;
    public static final Supplier<Block> YELLOW_CRATE;
    public static final Supplier<Block> LIME_CRATE;
    public static final Supplier<Block> GREEN_CRATE;
    public static final Supplier<Block> CYAN_CRATE;
    public static final Supplier<Block> LIGHT_BLUE_CRATE;
    public static final Supplier<Block> BLUE_CRATE;
    public static final Supplier<Block> PURPLE_CRATE;
    public static final Supplier<Block> MAGENTA_CRATE;
    public static final Supplier<Block> PINK_CRATE;

    public static final Supplier<Block> WAXED_IRON_BLOCK;
    public static final Supplier<Block> BLACKENED_IRON_BLOCK;
    public static final Supplier<Block> BLACKENED_IRON_GRATE;
    public static final Supplier<Block> BLACKENED_IRON_BARS;
    public static final Supplier<Block> BLACKENED_IRON_DOOR;
    public static final Supplier<Block> BLACKENED_IRON_TRAPDOOR;

    public static final Supplier<Block> REDSTONE_LED;
    public static final Supplier<Block> SAUCER_LIGHT;

    public static final Supplier<Block> COPPER_FIRE;

    private static Supplier<Block> addBlock(Params params) {
        RegSupplier<? extends Block> regSupplier = RegHelper.registerBlock(
                Condiments.loc(params.id),
                params.blockFactory
        );

        Supplier<Block> blockSupplier = regSupplier::get;

        if (params.createItem)
            ModItems.addItem(params.getItemParams(blockSupplier));

        if (params.renderType != null && PlatHelper.getPhysicalSide().isClient())
            ALT_RENDERED_BLOCKS.put(blockSupplier, params.renderType);

        return blockSupplier;
    }

    public static void init() {}

    static {
        RAIL_INTERSECTION = addBlock(new Params("rail_intersection",  () -> new RailIntersectionBlock(VersionHelper.copyProperties(Blocks.RAIL))).cutout());
        ANALOG_RAIL = addBlock(new Params("analog_rail", () -> new AnalogRailBlock(VersionHelper.copyProperties(Blocks.POWERED_RAIL))).cutout());

        CRATE = addCrate("crate", null);
        WHITE_CRATE = addCrate("white_crate", DyeColor.WHITE);
        LIGHT_GRAY_CRATE = addCrate("light_gray_crate", DyeColor.LIGHT_GRAY);
        GRAY_CRATE = addCrate("gray_crate", DyeColor.GRAY);
        BLACK_CRATE = addCrate("black_crate", DyeColor.BLACK);
        BROWN_CRATE = addCrate("brown_crate", DyeColor.BROWN);
        RED_CRATE = addCrate("red_crate", DyeColor.RED);
        ORANGE_CRATE = addCrate("orange_crate", DyeColor.ORANGE);
        YELLOW_CRATE = addCrate("yellow_crate", DyeColor.YELLOW);
        LIME_CRATE = addCrate("lime_crate", DyeColor.LIME);
        GREEN_CRATE = addCrate("green_crate", DyeColor.GREEN);
        CYAN_CRATE = addCrate("cyan_crate", DyeColor.CYAN);
        LIGHT_BLUE_CRATE = addCrate("light_blue_crate", DyeColor.LIGHT_BLUE);
        BLUE_CRATE = addCrate("blue_crate", DyeColor.BLUE);
        PURPLE_CRATE = addCrate("purple_crate", DyeColor.PURPLE);
        MAGENTA_CRATE = addCrate("magenta_crate", DyeColor.MAGENTA);
        PINK_CRATE = addCrate("pink_crate", DyeColor.PINK);

        WAXED_IRON_BLOCK = addBlock(new Params("waxed_iron_block", () -> new WaxedIronBlock(VersionHelper.copyProperties(Blocks.IRON_BLOCK))));
        BLACKENED_IRON_BLOCK = addBlock(new Params("blackened_iron_block", () -> new Block(VersionHelper.copyProperties(Blocks.IRON_BLOCK))));
        BLACKENED_IRON_GRATE = addBlock(new Params("blackened_iron_grate", () -> new WaterloggedTransparentBlock(VersionHelper.copyProperties(Blocks.IRON_BARS))).cutout());
        BLACKENED_IRON_BARS = addBlock(new Params("blackened_iron_bars", () -> new IronBarsBlock(VersionHelper.copyProperties(Blocks.IRON_BARS))).cutout());
        BLACKENED_IRON_DOOR = addBlock(new Params("blackened_iron_door", () -> new DoorBlock(
                //? if < 1.21 {
                /*VersionHelper.copyProperties(Blocks.IRON_DOOR), BlockSetType.IRON
                *///?} else
                BlockSetType.IRON, VersionHelper.copyProperties(Blocks.IRON_DOOR)
        )).cutout());
        BLACKENED_IRON_TRAPDOOR = addBlock(new Params("blackened_iron_trapdoor", () -> new TrapDoorBlock(
                //? if < 1.21 {
                /*VersionHelper.copyProperties(Blocks.IRON_TRAPDOOR), BlockSetType.IRON
                *///?} else
                BlockSetType.IRON, VersionHelper.copyProperties(Blocks.IRON_TRAPDOOR)
        )).cutout());

        REDSTONE_LED = addBlock(new Params("redstone_led", () -> new RedstoneLedBlock(BlockBehaviour.Properties.of()
                .strength(0.3F).sound(SoundType.GLASS).lightLevel(state -> 1).emissiveRendering(ModBlocks::always)
        )).cutout());
        SAUCER_LIGHT = addBlock(new Params("saucer_light", () -> new SaucerLightBlock(VersionHelper.copyProperties(Blocks.LANTERN)
                .lightLevel(state -> (Boolean)state.getValue(BlockStateProperties.LIT) ? 15 : 0)
                .pushReaction(PushReaction.NORMAL)
        )));

        COPPER_FIRE = addBlock(new Params("copper_fire", () -> new CopperFireBlock(VersionHelper.copyProperties(Blocks.SOUL_FIRE).mapColor(MapColor.COLOR_LIGHT_GREEN))).noItem().cutout());
    }

    public static class Params {
        public final String id;
        public final Supplier<? extends Block> blockFactory;
        public boolean createItem = true;
        public String renderType = null;

        public Params(String id, Supplier<? extends Block> blockFactory) {
            this.id = id;
            this.blockFactory = blockFactory;
        }

        public ModItems.Params getItemParams(Supplier<? extends Block>  block) {
            return new ModItems.Params(id, () -> this.getItem(block));
        }

        public BlockItem getItem(Supplier<? extends Block>  block) {
            return new BlockItem(block.get(), new Item.Properties());
        }

        public Params noItem() {
            createItem = false;
            return this;
        }

        public Params cutout() {
            renderType = "CUTOUT";
            return this;
        }
    }

    public static class CrateParams extends Params {
        CrateParams(String id, Supplier<? extends Block> blockFactory) {
            super(id, blockFactory);
        }

        @Override
        public BlockItem getItem(Supplier<? extends Block>  block) {
            return new CrateItem(block.get(), new Item.Properties()
                    //? if > 1.21
                    .component(ModComponents.CRATE_CONTENTS.get(), CrateContents.EMPTY)
            );
        }
    }

    private static Supplier<Block> addCrate(String id, DyeColor color) {
        return addBlock(new CrateParams(id, () -> new CrateBlock(color, VersionHelper.copyProperties(Blocks.BARREL).pushReaction(PushReaction.DESTROY))));
    }

    public static Block[] getCrates() {
        return new Block[]{
                CRATE.get(),
                WHITE_CRATE.get(),
                LIGHT_GRAY_CRATE.get(),
                GRAY_CRATE.get(),
                BLACK_CRATE.get(),
                BROWN_CRATE.get(),
                RED_CRATE.get(),
                ORANGE_CRATE.get(),
                YELLOW_CRATE.get(),
                LIME_CRATE.get(),
                GREEN_CRATE.get(),
                CYAN_CRATE.get(),
                LIGHT_BLUE_CRATE.get(),
                BLUE_CRATE.get(),
                PURPLE_CRATE.get(),
                MAGENTA_CRATE.get(),
                PINK_CRATE.get()
        };
    }

    public static boolean always(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return true;
    }
}
