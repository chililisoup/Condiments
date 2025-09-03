package dev.chililisoup.condiments.neoforge.item;

import dev.chililisoup.condiments.block.entity.CrateBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class CrateItemHandler extends ItemStackHandler {
    private final CrateBlockEntity crateBlockEntity;

    public CrateItemHandler(CrateBlockEntity crateBlockEntity) {
        this.crateBlockEntity = crateBlockEntity;
    }

    @Override
    public int getSlots() {
        return this.crateBlockEntity.getContainerSize();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return this.crateBlockEntity.getItem(slot);
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        this.crateBlockEntity.setItem(slot, stack);
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.crateBlockEntity.getMaxStackSize();
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return this.crateBlockEntity.insertIntoSlot(slot, stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return this.crateBlockEntity.extractFromSlot(slot, amount, simulate);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return this.crateBlockEntity.canPlaceItem(slot, stack);
    }
}
