//? if forgeLike || < 1.21 {
/*package dev.chililisoup.condiments.item;

import dev.chililisoup.condiments.block.entity.CrateBlockEntity;
import org.jetbrains.annotations.NotNull;

//? if forgeLike {
/^import net.minecraft.world.item.ItemStack;
^///?}

//? if neoforge {
/^import net.neoforged.neoforge.items.ItemStackHandler;
^///?} else if forge {
/^import net.minecraftforge.items.ItemStackHandler;
^///?} else {
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
//?}

@SuppressWarnings("UnstableApiUsage")
public class CrateItemHandler extends ItemStackHandler {
    private final CrateBlockEntity crateBlockEntity;

    public CrateItemHandler(CrateBlockEntity crateBlockEntity) {
        //? if fabric
        super(crateBlockEntity.getSlottedStacks());

        this.crateBlockEntity = crateBlockEntity;
    }

    @Override
    public boolean isItemValid(
            int slot,
            //? if fabric {
            @NotNull ItemVariant stack, int count
            //?} else
            /^@NotNull ItemStack stack^/
    ) {
        return this.crateBlockEntity.canPlaceItem(slot, stack/^? fabric {^/ .toStack() /^?}^/);
    }

    //? if fabric {
    @Override
    protected void onContentsChanged(int slot) {
        this.crateBlockEntity.setItem(slot, this.getStackInSlot(slot));
    }
    //?} else {
    /^@Override
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
    ^///?}
}
*///?}