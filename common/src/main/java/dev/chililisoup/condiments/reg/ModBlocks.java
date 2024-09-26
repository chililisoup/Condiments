package dev.chililisoup.condiments.reg;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.chililisoup.condiments.block.*;
import dev.chililisoup.condiments.item.CrateItem;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.function.Supplier;

public class ModBlocks {
    public static Supplier<Block> ANALOG_RAIL;
    //public static Supplier<Block> WAXED_RAIL;

    public static Supplier<Block> CRATE;
    public static Supplier<Block> WHITE_CRATE;
    public static Supplier<Block> LIGHT_GRAY_CRATE;
    public static Supplier<Block> GRAY_CRATE;
    public static Supplier<Block> BLACK_CRATE;
    public static Supplier<Block> BROWN_CRATE;
    public static Supplier<Block> RED_CRATE;
    public static Supplier<Block> ORANGE_CRATE;
    public static Supplier<Block> YELLOW_CRATE;
    public static Supplier<Block> LIME_CRATE;
    public static Supplier<Block> GREEN_CRATE;
    public static Supplier<Block> CYAN_CRATE;
    public static Supplier<Block> LIGHT_BLUE_CRATE;
    public static Supplier<Block> BLUE_CRATE;
    public static Supplier<Block> PURPLE_CRATE;
    public static Supplier<Block> MAGENTA_CRATE;
    public static Supplier<Block> PINK_CRATE;

    public static Supplier<Block> BLACKENED_IRON_BLOCK;
    public static Supplier<Block> WAXED_IRON_BLOCK;

    public static Supplier<Block> REDSTONE_LED;

    @ExpectPlatform
    private static Supplier<Block> addBlock(Params params) {
        throw new AssertionError();
    }

    public static void init() {
        addPolishedLogs("oak", MapColor.WOOD, MapColor.WOOD);
        addPolishedLogs("spruce", MapColor.PODZOL, MapColor.PODZOL);
        addPolishedLogs("birch", MapColor.SAND, MapColor.SAND);
        addPolishedLogs("jungle", MapColor.DIRT, MapColor.DIRT);
        addPolishedLogs("acacia", MapColor.COLOR_ORANGE, MapColor.COLOR_ORANGE);
        addPolishedLogs("dark_oak", MapColor.COLOR_BROWN, MapColor.COLOR_BROWN);
        addPolishedLogs("mangrove", MapColor.COLOR_RED, MapColor.COLOR_RED);
        addPolishedLogs("cherry", MapColor.TERRACOTTA_WHITE, MapColor.TERRACOTTA_PINK, SoundType.CHERRY_WOOD);

        addPolishedNetherLogs("crimson", MapColor.CRIMSON_STEM);
        addPolishedNetherLogs("warped", MapColor.WARPED_STEM);

        addFlammableWall("oak", Blocks.OAK_FENCE);
        addFlammableWall("spruce", Blocks.SPRUCE_FENCE);
        addFlammableWall("birch", Blocks.BIRCH_FENCE);
        addFlammableWall("jungle", Blocks.JUNGLE_FENCE);
        addFlammableWall("acacia", Blocks.ACACIA_FENCE);
        addFlammableWall("dark_oak", Blocks.DARK_OAK_FENCE);
        addFlammableWall("mangrove", Blocks.MANGROVE_FENCE);
        addFlammableWall("cherry", Blocks.CHERRY_FENCE);
        addFlammableWall("bamboo", Blocks.BAMBOO_FENCE);

        addWall("crimson", Blocks.CRIMSON_FENCE);
        addWall("warped", Blocks.WARPED_FENCE);

        addFlammableAccent("oak", Blocks.OAK_FENCE);
        addFlammableAccent("spruce", Blocks.SPRUCE_FENCE);
        addFlammableAccent("birch", Blocks.BIRCH_FENCE);
        addFlammableAccent("jungle", Blocks.JUNGLE_FENCE);
        addFlammableAccent("acacia", Blocks.ACACIA_FENCE);
        addFlammableAccent("dark_oak", Blocks.DARK_OAK_FENCE);
        addFlammableAccent("mangrove", Blocks.MANGROVE_FENCE);
        addFlammableAccent("cherry", Blocks.CHERRY_FENCE);

        addAccent("crimson", Blocks.CRIMSON_FENCE);
        addAccent("warped", Blocks.WARPED_FENCE);

        addBlock(new Params("rail_intersection",  () -> new RailIntersectionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.RAIL))).creativeTabs("REDSTONE_BLOCKS").cutout());
        ANALOG_RAIL = addBlock(new Params("analog_rail", () -> new AnalogRailBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POWERED_RAIL))).creativeTabs("REDSTONE_BLOCKS").cutout());
        //WAXED_RAIL = addBlock(new Params("waxed_rail", () -> new WaxedRail(BlockBehaviour.Properties.ofFullCopy(Blocks.RAIL))).creativeTabs("REDSTONE_BLOCKS").cutout());

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

        BLACKENED_IRON_BLOCK = addBlock(new Params("blackened_iron_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK))).creativeTabs("BUILDING_BLOCKS"));
        addBlock(new Params("blackened_iron_bars", () -> new IronBarsBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BARS))).creativeTabs("BUILDING_BLOCKS").cutout());
        addBlock(new Params("blackened_iron_door", () -> new DoorBlock(BlockSetType.IRON, BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_DOOR))).creativeTabs("BUILDING_BLOCKS", "REDSTONE_BLOCKS").cutout());
        addBlock(new Params("blackened_iron_trapdoor", () -> new TrapDoorBlock(BlockSetType.IRON, BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_TRAPDOOR))).creativeTabs("BUILDING_BLOCKS", "REDSTONE_BLOCKS").cutout());
        addBlock(new Params("blackened_iron_grate", () -> new WaterloggableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BARS))).creativeTabs("BUILDING_BLOCKS").cutout());

        WAXED_IRON_BLOCK = addBlock(new Params("waxed_iron_block", () -> new WaxedIronBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK))).creativeTabs("BUILDING_BLOCKS"));

        REDSTONE_LED = addBlock(new Params("redstone_led", () -> new RedstoneLedBlock(BlockBehaviour.Properties.of().strength(0.3F).sound(SoundType.GLASS))).creativeTabs("FUNCTIONAL_BLOCKS", "REDSTONE_BLOCKS").cutout());
    }



    private static BlockBehaviour.Properties polishedLog(MapColor topMapColor, MapColor sideMapColor, SoundType soundType) {
        return BlockBehaviour.Properties.of().mapColor((blockState) ->
                blockState.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? topMapColor : sideMapColor
        ).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(soundType).ignitedByLava().pushReaction(PushReaction.PUSH_ONLY);
    }

    private static BlockBehaviour.Properties polishedWood(MapColor mapColor, SoundType soundType) {
        return BlockBehaviour.Properties.of().mapColor(mapColor).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(soundType).ignitedByLava().pushReaction(PushReaction.PUSH_ONLY);
    }

    private static BlockBehaviour.Properties polishedNetherLog(MapColor mapColor) {
        return BlockBehaviour.Properties.of().mapColor(mapColor).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.STEM).pushReaction(PushReaction.PUSH_ONLY);
    }

    private static void addPolishedLogs(String type, MapColor topMapColor, MapColor sideMapColor) {
        addPolishedLogs(type, topMapColor, sideMapColor, SoundType.WOOD);
    }

    private static void addPolishedLogs(String type, MapColor topMapColor, MapColor sideMapColor, SoundType soundType) {
        addBlock(new Params("polished_" + type + "_log",  () -> new RotatedPillarBlock(polishedLog(topMapColor, sideMapColor, soundType))).flammable().creativeTabs("BUILDING_BLOCKS"));
        addBlock(new Params("polished_" + type + "_wood",  () -> new RotatedPillarBlock(polishedWood(sideMapColor, soundType))).flammable().creativeTabs("BUILDING_BLOCKS"));
    }

    private static void addPolishedNetherLogs(String type, MapColor mapColor) {
        addBlock(new Params("polished_" + type + "_stem",  () -> new RotatedPillarBlock(polishedNetherLog(mapColor))).creativeTabs("BUILDING_BLOCKS"));
        addBlock(new Params("polished_" + type + "_hyphae",  () -> new RotatedPillarBlock(polishedNetherLog(mapColor))).creativeTabs("BUILDING_BLOCKS"));
    }

    private static void addFlammableWall(String type, Block parent) {
        addBlock(new Params(type + "_wall", () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(parent).forceSolidOn())).flammable().creativeTabs("BUILDING_BLOCKS"));
    }

    private static void addWall(String type, Block parent) {
        addBlock(new Params(type + "_wall", () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(parent).forceSolidOn())).creativeTabs("BUILDING_BLOCKS"));
    }

    private static void addFlammableAccent(String type, Block parent) {
        addBlock(new Params(type + "_accent", () -> new AccentBlock(BlockBehaviour.Properties.ofFullCopy(parent))).flammable().creativeTabs("BUILDING_BLOCKS"));
    }

    private static void addAccent(String type, Block parent) {
        addBlock(new Params(type + "_accent", () -> new AccentBlock(BlockBehaviour.Properties.ofFullCopy(parent))).creativeTabs("BUILDING_BLOCKS"));
    }

    private static Supplier<Block> addCrate(String id, DyeColor color) {
        return addBlock(new CrateParams(id, () -> new CrateBlock(color, BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL).pushReaction(PushReaction.DESTROY))).creativeTabs("FUNCTIONAL_BLOCKS", "COLORED_BLOCKS"));
    }





    public static class Params {
        public final String id;
        public final Supplier<? extends Block> blockFactory;
        public boolean flammable = false;
        public String[] creativeTabs = new String[]{};
        public String renderType = null;

        public Params(String id, Supplier<? extends Block> blockFactory) {
            this.id = id;
            this.blockFactory = blockFactory;
        }

        public ModItems.Params getItemParams(Supplier<? extends Block>  block) {
            return new ModItems.Params(id, () -> new BlockItem(block.get(), new Item.Properties())).creativeTabs(creativeTabs);
        }

        public BlockItem getItem(Block block) {
            return new BlockItem(block, new Item.Properties());
        }

        public Params flammable() {
            flammable = true;
            return this;
        }

        public Params creativeTabs(String... tabs) {
            creativeTabs = tabs;
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
        public ModItems.Params getItemParams(Supplier<? extends Block>  block) {
            return new ModItems.Params(id, () -> new CrateItem(block.get(), new Item.Properties())).creativeTabs(creativeTabs);
        }

        @Override
        public BlockItem getItem(Block block) {
            return new CrateItem(block, new Item.Properties());
        }
    }
}
