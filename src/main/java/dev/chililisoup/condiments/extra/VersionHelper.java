package dev.chililisoup.condiments.extra;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class VersionHelper {
    public static boolean itemsMatch(ItemStack first, ItemStack second) {
        //? if < 1.21 {
        /*return ItemStack.isSameItemSameTags(first, second);
        *///?} else
        return ItemStack.isSameItemSameComponents(first, second);
    }

    public static ResourceLocation resourceLocation(String namespace, String path) {
        //? if < 1.21 {
        /*return new ResourceLocation(namespace, path);
        *///?} else
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    public static BlockBehaviour.Properties copyProperties(BlockBehaviour blockBehaviour) {
        //? if < 1.21 {
        /*return BlockBehaviour.Properties.copy(blockBehaviour);
        *///?} else
        return BlockBehaviour.Properties.ofFullCopy(blockBehaviour);
    }

    //? if < 1.21 {
    /*public static int size(net.minecraft.world.inventory.CraftingContainer input) {
        return input.getContainerSize();
    }
    *///?} else {
    public static int size(net.minecraft.world.item.crafting.CraftingInput input) {
        return input.size();
    }
    //?}
}
