//? if forge_like || < 1.21 {
/*package dev.chililisoup.condiments.compat.create;

import com.simibubi.create.api.contraption.storage.SyncedMountedStorage;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.content.contraptions.Contraption;
import dev.chililisoup.condiments.block.entity.CrateBlockEntity;
import dev.chililisoup.condiments.block.entity.CrateContents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//? if fabric {
import com.simibubi.create.api.contraption.storage.item.simple.SimpleMountedStorage;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

import java.util.Iterator;
import java.util.List;
//?} else {
/^import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.function.Function;
^///?}

//? if < 1.21 {
/^import com.mojang.serialization.Codec;
^///?} else
import com.mojang.serialization.MapCodec;

//? if fabric
@SuppressWarnings("UnstableApiUsage")
public class CrateMountedStorage
        extends /^? if forge_like {^/ /^MountedItemStorage ^//^?} else {^/ SimpleMountedStorage /^?}^/
        implements SyncedMountedStorage {
    public static final /^? if < 1.21 {^/ /^Codec ^//^?} else {^/ MapCodec /^?}^/<CrateMountedStorage> CODEC =
            codec(CrateMountedStorage::new);

    private boolean dirty;

    //? if forge_like {
    /^private final CrateContents.SlottedMutable contents;

    public CrateMountedStorage(MountedItemStorageType<?> type, CrateContents contents) {
        super(type);
        this.contents = contents.toSlottedMutable();
    }

    public CrateMountedStorage(CrateContents contents) {
        this(CreateCompat.CRATE_MOUNTED_STORAGE.get(), contents);
    }

    protected CrateContents getContents() {
        return this.contents.toImmutable();
    }
    ^///?} else {
    public CrateMountedStorage(MountedItemStorageType<?> type, ItemStackHandler wrapper) {
        super(type, wrapper);
    }

    public CrateMountedStorage(ItemStackHandler wrapper) {
        this(CreateCompat.CRATE_MOUNTED_STORAGE.get(), wrapper);
    }
    //?}

    @Override
    public boolean isDirty() {
        return this.dirty;
    }

    public void markDirty() {
        this.dirty = true;
    }

    @Override
    public void markClean() {
        this.dirty = false;
    }

    private void loadIntoCrate(CrateBlockEntity crateBlockEntity) {
        //? if fabric {
        List<SingleSlotStorage<ItemVariant>> slots = this.getSlots();
        ItemVariant present = null;
        int count = 0;

        for (SingleSlotStorage<ItemVariant> slot : slots) {
            if (slot.isResourceBlank()) continue;
            if (present == null) present = slot.getResource();
            count += (int) slot.getAmount();
        }

        CrateContents.Mutable contents = crateBlockEntity.getContents().toMutable();
        if (count != 0 || !contents.isLocked())
            contents.setItemType(present == null ? null : present.toStack());
        contents.setCount(count);

        crateBlockEntity.loadCrateContents(contents.toImmutable());
        //?} else
        /^crateBlockEntity.loadCrateContents(this.getContents());^/
    }

    @Override
    public void afterSync(Contraption contraption, BlockPos localPos) {
        if (contraption.getBlockEntityClientSide(localPos) instanceof CrateBlockEntity crateBlockEntity)
            this.loadIntoCrate(crateBlockEntity);
    }

    @Override
    public void unmount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity blockEntity) {
        if (blockEntity instanceof CrateBlockEntity crateBlockEntity)
            this.loadIntoCrate(crateBlockEntity);
    }

    //? if forge_like {
    /^@Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        this.markDirty();
        this.contents.setStackInSlot(slot, stack);
    }

    @Override
    public int getSlots() {
        return CrateContents.maxStacks();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return this.contents.getSlot(slot);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        this.markDirty();
        return this.contents.insertIntoSlot(slot, stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        this.markDirty();
        return this.contents.extractFromSlot(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.contents.getMaxStackSize();
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return this.contents.canAdd(stack);
    }

    @Override
    public boolean handleInteraction(ServerPlayer player, Contraption contraption, StructureTemplate.StructureBlockInfo info) {
        return false;
    }

    public static <T extends CrateMountedStorage> /^¹? if < 1.21 {¹^/ Codec /^¹?} else {¹^/ /^¹MapCodec ¹^//^¹?}¹^/<T> codec(Function<CrateContents, T> factory) {
        return CrateContents.CODEC.xmap(factory, CrateMountedStorage::getContents)
                /^¹? if >= 1.21 {¹^/ /^¹.fieldOf("value") ¹^//^¹?}¹^/;
    }
    ^///?} else {
    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        this.markDirty();
        super.setStackInSlot(slot, stack);
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        this.markDirty();
        return super.insert(resource, maxAmount, transaction);
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        this.markDirty();
        return super.extract(resource, maxAmount, transaction);
    }

    @Override
    public long insertSlot(int slot, ItemVariant resource, long maxAmount, TransactionContext transaction) {
        this.markDirty();
        return super.insertSlot(slot, resource, maxAmount, transaction);
    }

    @Override
    public long extractSlot(int slot, ItemVariant resource, long maxAmount, TransactionContext transaction) {
        this.markDirty();
        return super.extractSlot(slot, resource, maxAmount, transaction);
    }

    @Override
    public @NotNull Iterator<StorageView<ItemVariant>> iterator() {
        this.markDirty();
        return super.iterator();
    }
    //?}
}
*///?}