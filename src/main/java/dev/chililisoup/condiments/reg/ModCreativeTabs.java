package dev.chililisoup.condiments.reg;

import dev.chililisoup.condiments.config.CommonConfig;
import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;

import static dev.chililisoup.condiments.reg.ModBlockSetVariants.*;

public class ModCreativeTabs {
    public static void init() {
        RegHelper.addItemsToTabsRegistration(ModCreativeTabs::registerItemsToTabs);
    }

    private static void registerItemsToTabs(RegHelper.ItemToTabEvent event) {
        if (CommonConfig.BLACKENED_IRON.get()) {
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
                    CreativeModeTabs.INGREDIENTS,
                    itemStack -> itemStack.is(Items.IRON_INGOT),
                    ModItems.BLACKENED_IRON_INGOT.get()
            );
        }

        if (CommonConfig.BRAZIER.get()) event.addAfter(
                CreativeModeTabs.FUNCTIONAL_BLOCKS,
                itemStack -> itemStack.is(Items.REDSTONE_LAMP),
                ModBlocks.BRAZIER.get()
        );

        if (CommonConfig.SAUCER_LIGHT.get()) {
            event.addAfter(
                    CreativeModeTabs.FUNCTIONAL_BLOCKS,
                    itemStack -> itemStack.is(Items.REDSTONE_LAMP),
                    ModBlocks.SAUCER_LIGHT.get()
            );

            event.addAfter(
                    CreativeModeTabs.REDSTONE_BLOCKS,
                    itemStack -> itemStack.is(Items.REDSTONE_LAMP),
                    ModBlocks.SAUCER_LIGHT.get()
            );
        }

        if (CommonConfig.REDSTONE_LED.get()) {
            event.addAfter(
                    CreativeModeTabs.FUNCTIONAL_BLOCKS,
                    itemStack -> itemStack.is(Items.REDSTONE_LAMP),
                    ModBlocks.REDSTONE_LED.get()
            );

            event.addAfter(
                    CreativeModeTabs.REDSTONE_BLOCKS,
                    itemStack -> itemStack.is(Items.REDSTONE_LAMP),
                    ModBlocks.REDSTONE_LED.get()
            );
        }

        if (CommonConfig.RAIL_INTERSECTION.get()) {
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
        }

        if (CommonConfig.ANALOG_RAIL.get()) {
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
        }

        if (CommonConfig.CRATES.get()) {
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
        }

        if (CommonConfig.WOOD_ACCENTS.get()) WOOD_ACCENTS.items.forEach((wood, item) -> event.addBefore(
                CreativeModeTabs.BUILDING_BLOCKS,
                itemStack -> itemStack.is(wood.getItemOfThis("fence")),
                item
        ));

        if (CommonConfig.WOOD_WALLS.get()) WOOD_WALLS.items.forEach((wood, item) -> event.addBefore(
                CreativeModeTabs.BUILDING_BLOCKS,
                itemStack -> itemStack.is(wood.getItemOfThis("fence")),
                item
        ));

        if (CommonConfig.POLISHED_WOOD.get()) {
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
}
