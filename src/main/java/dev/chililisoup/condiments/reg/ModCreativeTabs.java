package dev.chililisoup.condiments.reg;

import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;

import static dev.chililisoup.condiments.reg.ModBlockSetVariants.*;

public class ModCreativeTabs {
    public static void init() {
        RegHelper.addItemsToTabsRegistration(ModCreativeTabs::registerItemsToTabs);
    }

    private static void registerItemsToTabs(RegHelper.ItemToTabEvent event) {
        event.addAfter(
                CreativeModeTabs.BUILDING_BLOCKS,
                itemStack -> itemStack.is(Items.CHAIN),
                ModBlocks.WAXED_IRON_BLOCK.get(),
                ModBlocks.BLACKENED_IRON_BLOCK.get(),
                ModBlocks.BLACKENED_IRON_GRATE.get(),
                ModBlocks.BLACKENED_IRON_BARS.get(),
                ModBlocks.BLACKENED_IRON_DOOR.get(),
                ModBlocks.BLACKENED_IRON_TRAPDOOR.get()
        );

        event.addAfter(
                CreativeModeTabs.FUNCTIONAL_BLOCKS,
                itemStack -> itemStack.is(Items.REDSTONE_LAMP),
                ModBlocks.REDSTONE_LED.get(),
                ModBlocks.SAUCER_LIGHT.get(),
                ModBlocks.BRAZIER.get()
        );

        event.addAfter(
                CreativeModeTabs.REDSTONE_BLOCKS,
                itemStack -> itemStack.is(Items.REDSTONE_LAMP),
                ModBlocks.REDSTONE_LED.get(),
                ModBlocks.SAUCER_LIGHT.get()
        );

        event.addAfter(
                CreativeModeTabs.REDSTONE_BLOCKS,
                itemStack -> itemStack.is(Items.RAIL),
                ModBlocks.RAIL_INTERSECTION.get()
        );

        event.addAfter(
                CreativeModeTabs.TOOLS_AND_UTILITIES,
                itemStack -> itemStack.is(Items.RAIL),
                ModBlocks.RAIL_INTERSECTION.get()
        );

        event.addAfter(
                CreativeModeTabs.REDSTONE_BLOCKS,
                itemStack -> itemStack.is(Items.POWERED_RAIL),
                ModBlocks.ANALOG_RAIL.get()
        );

        event.addAfter(
                CreativeModeTabs.TOOLS_AND_UTILITIES,
                itemStack -> itemStack.is(Items.POWERED_RAIL),
                ModBlocks.ANALOG_RAIL.get()
        );

        event.addAfter(
                CreativeModeTabs.FUNCTIONAL_BLOCKS,
                itemStack -> itemStack.is(Items.PINK_SHULKER_BOX),
                ModBlocks.getCrates()
        );

        event.addAfter(
                CreativeModeTabs.COLORED_BLOCKS,
                itemStack -> itemStack.is(Items.PINK_SHULKER_BOX),
                ModBlocks.getCrates()
        );

        event.addAfter(
                CreativeModeTabs.INGREDIENTS,
                itemStack -> itemStack.is(Items.IRON_INGOT),
                ModItems.BLACKENED_IRON_INGOT.get()
        );

        WOOD_WALLS.items.forEach((wood, item) -> event.addAfter(
                CreativeModeTabs.BUILDING_BLOCKS,
                itemStack -> itemStack.is(wood.getItemOfThis("slab")),
                item
        ));

        WOOD_ACCENTS.items.forEach((wood, item) -> event.addAfter(
                CreativeModeTabs.BUILDING_BLOCKS,
                itemStack -> itemStack.is(wood.getItemOfThis("slab")),
                item
        ));

        POLISHED_WOOD.items.forEach((wood, item) -> event.addAfter(
                CreativeModeTabs.BUILDING_BLOCKS,
                itemStack -> itemStack.is(wood.getItemOfThis("stripped_wood")),
                item
        ));

        POLISHED_LOGS.items.forEach((wood, item) -> event.addAfter(
                CreativeModeTabs.BUILDING_BLOCKS,
                itemStack -> itemStack.is(wood.getItemOfThis("stripped_wood")),
                item
        ));
    }
}
