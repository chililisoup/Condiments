package dev.chililisoup.condiments.extra;

public final class VersionHelper {
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
