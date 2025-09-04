//? if forgeLike {
/*package dev.chililisoup.condiments.compat.create;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.api.contraption.storage.SyncedMountedStorage;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorage;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.content.contraptions.Contraption;
import dev.chililisoup.condiments.block.entity.CrateBlockEntity;
import dev.chililisoup.condiments.block.entity.CrateContents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class CrateMountedStorage extends MountedItemStorage implements SyncedMountedStorage {
    public static final MapCodec<CrateMountedStorage> CODEC = codec(CrateMountedStorage::new);

    private final CrateContents.SlottedMutable contents;
    private boolean dirty;

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

    @Override
    public void afterSync(Contraption contraption, BlockPos localPos) {
        if (contraption.presentBlockEntities.get(localPos) instanceof CrateBlockEntity crateBlockEntity)
            crateBlockEntity.loadCrateContents(this.getContents());
    }

    @Override
    public void unmount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity blockEntity) {
        if (blockEntity instanceof CrateBlockEntity crateBlockEntity)
            crateBlockEntity.loadCrateContents(this.getContents());
    }

    @Override
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

    public static <T extends CrateMountedStorage> MapCodec<T> codec(Function<CrateContents, T> factory) {
        return CrateContents.CODEC.xmap(factory, CrateMountedStorage::getContents).fieldOf("value");
    }
}
*///?}