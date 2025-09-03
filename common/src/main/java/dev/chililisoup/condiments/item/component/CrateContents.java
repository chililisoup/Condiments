package dev.chililisoup.condiments.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.chililisoup.condiments.block.entity.CrateBlockEntity;
import dev.chililisoup.condiments.config.CommonConfig;
import dev.chililisoup.condiments.reg.ModComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record CrateContents(Optional<ItemRecord> itemRecord, int count, Optional<Boolean> locked) {
    public static final CrateContents EMPTY = new CrateContents();
    public static final Codec<CrateContents> CODEC;
    public static final StreamCodec<RegistryFriendlyByteBuf, CrateContents> STREAM_CODEC;

    public CrateContents(Optional<ItemRecord> itemRecord, int count) {
        this(itemRecord, count, Optional.empty());
    }

    public CrateContents() {
        this(Optional.empty(), 0);
    }

    public static CrateContents of(CrateBlockEntity blockEntity) {
        Optional<ItemRecord> itemRecord = blockEntity.shouldSaveItemType() ?
                Optional.of(ItemRecord.of(blockEntity.getItemType())) :
                Optional.empty();

        Optional<Boolean> locked = blockEntity.isLocked() ?
                Optional.of(true) :
                Optional.empty();

        return new CrateContents(itemRecord, blockEntity.getCount(), locked);
    }

    public Optional<ItemStack> item() {
        return this.itemRecord.flatMap(itemRecord -> Optional.of(itemRecord.asItemStack()));
    }

    public boolean isLocked() {
        return this.locked.orElse(false);
    }

    public static int maxStacks() {
        return CommonConfig.CRATE_MAX_CONTAINED_STACKS.get();
    }

    public int capacity() {
        int stackCount = maxStacks();

        return this.item().map(
                stack -> stack.getMaxStackSize() * stackCount
        ).orElse(0);
    }

    public float fillPercent() {
        if (this.count <= 0 && !this.isLocked()) return -1;
        if (this.item().isEmpty()) return -1;
        return (float) this.count / this.capacity();
    }

    public @NotNull String toString() {
        return String.format("%s x %d, %s", this.item(), this.count, this.isLocked() ? "LOCKED" : "UNLOCKED");
    }

    public static boolean isItemUnsafe(ItemStack stack) {
        if (stack.isEmpty()) return true;

        if (stack.has(DataComponents.CONTAINER)) {
            ItemContainerContents containerContents = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
            if (containerContents.nonEmptyStream().findAny().isPresent()) return true;
        }

        if (stack.has(ModComponents.CRATE_CONTENTS.get())) {
            if (!CommonConfig.CRATES_CONTAIN_EMPTY_CRATES.get()) return true;

            CrateContents crateContents = stack.getOrDefault(ModComponents.CRATE_CONTENTS.get(), CrateContents.EMPTY);
            return crateContents.count > 0;
        }

        return false;
    }

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemRecord.ITEM_CODEC.optionalFieldOf("item").forGetter(CrateContents::itemRecord),
                Codec.INT.fieldOf("count").forGetter(CrateContents::count),
                Codec.BOOL.optionalFieldOf("locked").forGetter(CrateContents::locked)
        ).apply(instance, CrateContents::new));

        STREAM_CODEC = StreamCodec.composite(
                ItemRecord.ITEM_STREAM_CODEC.apply(ByteBufCodecs::optional),
                CrateContents::itemRecord,
                ByteBufCodecs.INT,
                CrateContents::count,
                ByteBufCodecs.BOOL.apply(ByteBufCodecs::optional),
                CrateContents::locked,
                CrateContents::new
        );
    }

    public record ItemRecord(Holder<Item> item, DataComponentPatch components) {
        public static final Codec<ItemRecord> ITEM_CODEC;
        public static final StreamCodec<RegistryFriendlyByteBuf, ItemRecord> ITEM_STREAM_CODEC;

        public ItemStack asItemStack() {
            return new ItemStack(this.item, 1, this.components);
        }

        public static ItemRecord of(ItemStack item) {
            return new ItemRecord(
                    item.getItemHolder(),
                    ((PatchedDataComponentMap) item.getComponents()).asPatch()
            );
        }

        static {
            ITEM_CODEC = ItemStack.CODEC.xmap(ItemRecord::of, ItemRecord::asItemStack);
            ITEM_STREAM_CODEC = ItemStack.STREAM_CODEC.map(ItemRecord::of, ItemRecord::asItemStack);
        }
    }

    // I think the optionals scattered around are from circumventing a bug in some other mod IIRC
    public static class Mutable {
        public Optional<ItemStack> item;
        public int count;
        private Optional<Boolean> locked;

        public Mutable(CrateContents contents) {
            this.count = contents.count;
            this.locked = contents.locked;
            this.item = contents.item();
        }

        public boolean isLocked() {
            return this.locked.orElse(false);
        }

        public void setLocked(boolean locked) {
            this.locked = locked ? Optional.of(true) : Optional.empty();
            if (!locked && this.count <= 0) this.item = Optional.empty();
        }

        private void updateItem() {
            if (this.count <= 0 && !this.isLocked()) this.item = Optional.empty();
        }

        public int getMaxStackSize() {
            return this.item.orElse(ItemStack.EMPTY).getMaxStackSize();
        }

        private int getMaxAmountToAdd(ItemStack stack) {
            int stackCount = maxStacks();

            return this.item.map(
                    itemStack -> Math.max(itemStack.getMaxStackSize() * stackCount - this.count, 0)
            ).orElseGet(() -> stack.getMaxStackSize() * stackCount);
        }

        public boolean canAdd(ItemStack stack) {
            if (isItemUnsafe(stack)) return false;

            return this.item.map(
                    itemStack -> ItemStack.isSameItemSameComponents(itemStack, stack)
            ).orElse(true);
        }

        public int getToAdd(ItemStack stack) {
            if (!this.canAdd(stack)) return 0;
            return Math.min(this.getMaxAmountToAdd(stack), stack.getCount());
        }

        public void addFromStack(ItemStack stack, int maxToAdd) {
            if (this.item.isEmpty())
                this.item = Optional.of(stack.copyWithCount(1));
            else if (!ItemStack.isSameItemSameComponents(this.item.get(), stack)) {
                return;
            }

            int amt = Math.min(maxToAdd, stack.getCount());
            stack.shrink(amt);
            this.count += amt;
        }

        public Optional<ItemStack> removeOne() {
            if (this.item.isEmpty() || this.count <= 0) return Optional.empty();

            this.count--;

            Optional<ItemStack> returnStack = Optional.of(this.item.get().copyWithCount(1));
            this.updateItem();
            return returnStack;
        }

        public Optional<ItemStack> removeOneStack() {
            if (this.item.isEmpty() || this.count <= 0) return Optional.empty();

            int amt = Math.min(this.item.get().getMaxStackSize(), this.count);
            this.count -= amt;

            Optional<ItemStack> returnStack = Optional.of(this.item.get().copyWithCount(amt));
            this.updateItem();
            return returnStack;
        }

        public ItemStack getHypotheticalSlot(int slot) {
            if (this.count <= 0 || this.item.isEmpty()) return ItemStack.EMPTY;

            int maxStackSize = this.getMaxStackSize();
            int before = slot * maxStackSize;
            int after = this.count - before;
            int count = Mth.clamp(after, 0, maxStackSize);

            return count > 0 ? this.item.get().copyWithCount(count) : ItemStack.EMPTY;
        }

        public void setStackInHypotheticalSlot(int slot, @NotNull ItemStack stack) {
            ItemStack slotStack = this.getHypotheticalSlot(slot);

            if (stack.isEmpty()) {
                if (slotStack.isEmpty()) return;
                this.count -= stack.getCount();
                this.updateItem();
                return;
            }

            if (slotStack.isEmpty()) this.count += stack.getCount();
            else this.count += stack.getCount() - slotStack.getCount();
            this.item = Optional.of(slotStack.copyWithCount(1));
        }

        public ItemStack insertIntoHypotheticalSlot(int slot, @NotNull ItemStack stack, boolean simulate) {
            ItemStack refStack = stack.copy();
            if (!this.canAdd(stack)) return refStack;

            ItemStack item = this.item.orElseGet(() -> refStack.copyWithCount(1));
            ItemStack slotStack = this.getHypotheticalSlot(slot);

            int freeSpace = item.getMaxStackSize() - slotStack.getCount();
            int toAdd = Math.min(refStack.getCount(), freeSpace);
            if (toAdd <= 0) return refStack;

            if (!simulate) {
                this.item = Optional.of(item);
                this.count += toAdd;
            }

            refStack.shrink(toAdd);
            return refStack;
        }

        public @NotNull ItemStack extractFromHypotheticalSlot(int slot, int amount, boolean simulate) {
            ItemStack slotStack = this.getHypotheticalSlot(slot);
            if (slotStack.isEmpty()) return ItemStack.EMPTY;

            int finalAmt = Math.min(slotStack.getCount(), amount);
            slotStack.setCount(finalAmt);

            if (!simulate) {
                this.count -= finalAmt;
                this.updateItem();
            }

            return slotStack;
        }

        public CrateContents toImmutable() {
            return new CrateContents(this.item.flatMap(item -> Optional.of(ItemRecord.of(item))), this.count, this.locked);
        }

        public String toString() {
            return this.toImmutable().toString();
        }
    }
}
