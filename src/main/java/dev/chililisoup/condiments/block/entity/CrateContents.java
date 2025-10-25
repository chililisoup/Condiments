package dev.chililisoup.condiments.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.chililisoup.condiments.config.CommonConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//? if < 1.21 {
/*import net.minecraft.nbt.CompoundTag;
*///?} else {
import dev.chililisoup.condiments.reg.ModComponents;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.component.ItemContainerContents;
//?}

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static dev.chililisoup.condiments.block.entity.CrateBlockEntity.COUNT_KEY;

public record CrateContents(@Nullable ItemRecord itemRecord, int count, Boolean locked, Boolean autoPickup) {
    public static final CrateContents EMPTY = new CrateContents();
    public static final Codec<CrateContents> CODEC;
    //? if >= 1.21
    public static final StreamCodec<RegistryFriendlyByteBuf, CrateContents> STREAM_CODEC;

    public CrateContents(@Nullable ItemRecord itemRecord, int count) {
        this(itemRecord, count, false, false);
    }

    public CrateContents() {
        this(null, 0);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private CrateContents(Optional<ItemRecord> itemRecord, int count, Optional<Boolean> locked, Optional<Boolean> autoPickup) {
        this(itemRecord.orElse(null), count, locked.orElse(false), autoPickup.orElse(false));
    }

    private Optional<ItemRecord> itemRecordOptional() {
        return Optional.ofNullable(this.itemRecord);
    }

    private Optional<Boolean> lockedOptional() {
        return this.locked ? Optional.of(true) : Optional.empty();
    }

    private Optional<Boolean> autoPickupOptional() {
        return this.autoPickup ? Optional.of(true) : Optional.empty();
    }

    public static CrateContents fromCrateItem(ItemStack crateItem) {
        //? if < 1.21 {
        /*CompoundTag compoundTag = crateItem.getTagElement("BlockEntityTag");
        if (compoundTag == null) return EMPTY;

        boolean locked = compoundTag.getBoolean("CrateLocked");
        boolean autoPickup = compoundTag.getBoolean("CrateAutoPickup");

        short count = 0;
        ItemRecord record = null;

        CompoundTag storageTag = compoundTag.getCompound("CrateItems").copy();
        if (!storageTag.isEmpty()) {
            count = storageTag.getShort(COUNT_KEY);
            storageTag.putShort(COUNT_KEY, (short) 1);

            ItemStack item = ItemStack.of(storageTag);
            if (!item.isEmpty()) record = ItemRecord.of(item);
        }

        return new CrateContents(record, count, locked, autoPickup);
        *///?} else
        return crateItem.getOrDefault(ModComponents.CRATE_CONTENTS.get(), EMPTY);
    }

    public void updateCrateItem(ItemStack crateItem) {
        //? if < 1.21 {
        /*CompoundTag compoundTag = crateItem.getOrCreateTagElement("BlockEntityTag");
        if (this.autoPickup) compoundTag.putBoolean("CrateAutoPickup", true);
        else compoundTag.remove("CrateAutoPickup");

        Optional<ItemStack> itemType = this.item();
        if (itemType.isEmpty() && !this.isLocked()) {
            compoundTag.remove("CrateItems");
            compoundTag.remove("CrateLocked");
            if (compoundTag.isEmpty()) crateItem.removeTagKey("BlockEntityTag");
            return;
        }

        compoundTag.putBoolean("CrateLocked", this.isLocked());

        if (itemType.isPresent()) {
            CompoundTag storageTag = itemType.get().save(new CompoundTag());
            storageTag.putShort(COUNT_KEY, (short) this.count);
            compoundTag.put("CrateItems", storageTag);
        }
        *///?} else {
        crateItem.set(ModComponents.CRATE_CONTENTS.get(), this);
        crateItem.set(DataComponents.MAX_STACK_SIZE, this.count > 0 ? 1 : CommonConfig.EMPTY_CRATE_STACK_SIZE.get());
        //?}
    }

    public Optional<ItemStack> item() {
        return this.itemRecordOptional().flatMap(itemRecord -> Optional.of(itemRecord.asItemStack()));
    }

    public boolean isLocked() {
        return this.locked;
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

    public Mutable toMutable() {
        return new Mutable(this);
    }

    public SlottedMutable toSlottedMutable() {
        return new SlottedMutable(this);
    }

    public @NotNull String toString() {
        return String.format("%s x %d, %s", this.item(), this.count, this.isLocked() ? "LOCKED" : "UNLOCKED");
    }

    //? if >= 1.21 {
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
    //?} else {
    /*public static boolean isItemUnsafe(ItemStack stack) {
        if (stack.isEmpty()) return true;

        CompoundTag compoundTag = stack.getTagElement("BlockEntityTag");
        if (compoundTag == null) return false;


        if (compoundTag.contains("Items", 9)) {
            net.minecraft.nbt.ListTag containerContents = compoundTag.getList("Items", 10);
            if (!containerContents.isEmpty()) return true;
        }

        if (compoundTag.contains("CrateItems")) {
            if (!CommonConfig.CRATES_CONTAIN_EMPTY_CRATES.get()) return true;
            return compoundTag.getCompound("CrateItems").getShort(COUNT_KEY) > 0;
        }

        return false;
    }
    *///?}

    static {
        CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemRecord.ITEM_CODEC.optionalFieldOf("item").forGetter(CrateContents::itemRecordOptional),
                Codec.INT.fieldOf(COUNT_KEY).forGetter(CrateContents::count),
                Codec.BOOL.optionalFieldOf("locked").forGetter(CrateContents::lockedOptional),
                Codec.BOOL.optionalFieldOf("autoPickup").forGetter(CrateContents::autoPickupOptional)
        ).apply(instance, CrateContents::new));


        //? if >= 1.21 {
        STREAM_CODEC = StreamCodec.composite(
                ItemRecord.ITEM_STREAM_CODEC.apply(ByteBufCodecs::optional),
                CrateContents::itemRecordOptional,
                ByteBufCodecs.INT,
                CrateContents::count,
                ByteBufCodecs.BOOL.apply(ByteBufCodecs::optional),
                CrateContents::lockedOptional,
                ByteBufCodecs.BOOL.apply(ByteBufCodecs::optional),
                CrateContents::autoPickupOptional,
                CrateContents::new
        );
        //?}
    }

    public record ItemRecord(
            Holder<Item> item,
            //? if < 1.21 {
            /*Optional<CompoundTag> components
            *///?} else
            DataComponentPatch components
    ) {
        public static final Codec<ItemRecord> ITEM_CODEC;
        //? if >= 1.21
        public static final StreamCodec<RegistryFriendlyByteBuf, ItemRecord> ITEM_STREAM_CODEC;

        public ItemStack asItemStack() {
            //? if < 1.21 {
            /*ItemStack stack = new ItemStack(this.item, 1);
            this.components.ifPresent(stack::setTag);
            return stack;
            *///?} else
            return new ItemStack(this.item, 1, this.components);
        }

        public static ItemRecord of(ItemStack item) {
            return new ItemRecord(
                    item.getItemHolder(),
                    //? if < 1.21 {
                    /*Optional.ofNullable(item.getTag())
                    *///?} else
                    ((PatchedDataComponentMap) item.getComponents()).asPatch()
            );
        }

        static {
            ITEM_CODEC = ItemStack.CODEC.xmap(ItemRecord::of, ItemRecord::asItemStack);
            //? if >= 1.21
            ITEM_STREAM_CODEC = ItemStack.STREAM_CODEC.map(ItemRecord::of, ItemRecord::asItemStack);
        }
    }

    // I think the optionals scattered around are from circumventing a bug in some other mod IIRC
    public static class Mutable {
        private ItemStack item = ItemStack.EMPTY;
        private int count;
        private boolean locked;
        private boolean autoPickup;

        private Mutable() {}

        public Mutable(CrateContents contents) {
            this.setValues(contents);
        }

        public Mutable setValues(CrateContents contents) {
            this.setLocked(contents.locked);
            this.setItemType(contents.item().orElse(null));
            this.setCount(contents.count);
            this.setAutoPickup(contents.autoPickup);
            return this;
        }

        public ItemStack getItemType(int count) {
            return this.item.copyWithCount(count);
        }

        public ItemStack getItemType() {
            return this.getItemType(1);
        }

        public void setItemType(@Nullable ItemStack stack) {
            this.item = stack != null ?
                    stack.copyWithCount(1) :
                    ItemStack.EMPTY;
        }

        public boolean isEmptyNoLock() {
            return this.getCount() <= 0 && !this.isLocked();
        }

        protected void updateItemType() {
            if (this.isEmptyNoLock()) this.setItemType(null);
        }

        public int getCount() {
            return this.count;
        }

        public void setCount(int count) {
            this.count = count;
            this.updateItemType();
        }

        public void grow(int increment) {
            this.setCount(this.getCount() + increment);
        }

        public void shrink(int decrement) {
            this.setCount(this.getCount() - decrement);
        }

        public void clear() {
            this.setCount(0);
        }

        public boolean isLocked() {
            return this.locked;
        }

        public void setLocked(boolean locked) {
            this.locked = locked;
            this.updateItemType();
        }

        public boolean isAutoPickup() {
            return this.autoPickup;
        }

        public void setAutoPickup(boolean autoPickup) {
            this.autoPickup = autoPickup;
        }

        public int getMaxStackSize() {
            return this.getItemType().getMaxStackSize();
        }

        public int getCountRemaining() {
            ItemStack itemType = this.getItemType();
            return itemType.isEmpty() ?
                    -1 :
                    Math.max(itemType.getMaxStackSize() * maxStacks() - this.getCount(), 0);
        }

        protected int getMaxAmountToAdd(ItemStack stack) {
            int countRemaining = this.getCountRemaining();
            return countRemaining == -1 ?
                    stack.getMaxStackSize() * maxStacks() :
                    countRemaining;
        }

        protected int getMaxAmountToAdd() {
            return this.getMaxAmountToAdd(this.getItemType());
        }

        public boolean canAdd(ItemStack stack) {
            if (isItemUnsafe(stack)) return false;
            ItemStack itemType = this.getItemType();

            return itemType.isEmpty() || ItemStack.isSameItemSameComponents(itemType, stack);
        }

        public int getToAdd(ItemStack stack) {
            if (!this.canAdd(stack)) return 0;
            return Math.min(this.getMaxAmountToAdd(stack), stack.getCount());
        }

        public int addFromStack(ItemStack stack, boolean simulate) {
            if (this.canAdd(stack)) this.setItemType(stack);
            else return 0;

            int amt = this.getToAdd(stack);
            if (amt == 0) return 0;

            stack.shrink(amt);
            if (!simulate) this.grow(amt);
            return amt;
        }

        public int addFromStack(ItemStack stack) {
            return this.addFromStack(stack, false);
        }

        public int addFromSlot(Slot slot, Player player) {
            ItemStack stack = slot.getItem();
            int amt = this.getToAdd(stack);
            if (amt == 0) return 0;
            return this.addFromStack(slot.safeTake(stack.getCount(), amt, player));
        }

        public ItemStack tryAddStack(ItemStack stack, boolean simulate) {
            ItemStack refStack = stack.copy();
            if (!canAdd(refStack)) return refStack;
            if (refStack.isEmpty()) return ItemStack.EMPTY;

            this.addFromStack(refStack, simulate);
            return refStack;
        }

        public boolean addAllInventory(Player player) {
            if (this.isEmptyNoLock()) return false;

            int addCount = player.getInventory().clearOrCountMatchingItems(
                    this::canAdd,
                    this.getMaxAmountToAdd(),
                    player.getInventory()
            );

            if (addCount <= 0) return false;
            this.grow(addCount);
            return true;
        }

        public ItemStack request(int amount) {
            ItemStack itemType = this.getItemType();
            if (itemType.isEmpty() || this.getCount() <= 0) return ItemStack.EMPTY;

            int amt = Math.min(amount, this.getCount());
            this.shrink(amt);

            this.updateItemType();
            return itemType.copyWithCount(amt);
        }

        public ItemStack requestOne() {
            return this.request(1);
        }

        public ItemStack requestOneStack() {
            ItemStack itemType = this.getItemType();
            return itemType.isEmpty() ?
                    ItemStack.EMPTY :
                    this.request(itemType.getMaxStackSize());
        }

        public CrateContents toImmutable() {
            this.updateItemType();
            ItemStack itemType = this.getItemType();

            return new CrateContents(
                    itemType.isEmpty() ? null : ItemRecord.of(itemType),
                    this.getCount(),
                    this.isLocked(),
                    this.isAutoPickup()
            );
        }

        public String toString() {
            return this.toImmutable().toString();
        }
    }

    public static class SlottedMutable extends Mutable {
        private final NonNullList<ItemStack> itemStacks;

        public SlottedMutable(CrateContents contents) {
            this.itemStacks = NonNullList.withSize(maxStacks(), ItemStack.EMPTY);
            this.setValues(contents);
        }

        @Override
        public Mutable setValues(CrateContents contents) {
            this.itemStacks.clear();
            return super.setValues(contents);
        }

        @Override
        public int getCount() {
            AtomicInteger count = new AtomicInteger();
            this.itemStacks.forEach(stack -> count.addAndGet(stack.getCount()));
            return count.get();
        }

        @Override
        public void setCount(int count) {
            ItemStack itemType = this.getItemType();
            if (count == 0 || itemType.isEmpty()) {
                this.itemStacks.clear();
            } else {
                int maxStackSize = itemType.getMaxStackSize();
                int remaining = count - this.getCount();
                if (remaining > 0) {
                    for (int i = 0; i < this.itemStacks.size() && remaining > 0; i++) {
                        ItemStack stack = this.itemStacks.get(i);

                        if (ItemStack.isSameItemSameComponents(stack, itemType)) {
                            int free = maxStackSize - stack.getCount();
                            if (free <= 0) continue;

                            int toAdd = Math.min(remaining, free);
                            remaining -= toAdd;
                            stack.grow(toAdd);
                        } else {
                            int toAdd = Math.min(remaining, maxStackSize);
                            this.itemStacks.set(i, itemType.copyWithCount(toAdd));
                            remaining -= toAdd;
                        }
                    }
                } else if (remaining < 0) {
                    remaining = Math.abs(remaining);

                    for (int i = this.itemStacks.size() - 1; i >= 0 && remaining > 0; i--) {
                        ItemStack stack = this.itemStacks.get(i);

                        if (!ItemStack.isSameItemSameComponents(stack, itemType))
                            continue;

                        int toRemove = Math.min(stack.getCount(), remaining);
                        remaining -= toRemove;
                        stack.shrink(toRemove);
                    }
                }
            }

            super.setCount(count);
        }

        public ItemStack getSlot(int slot) {
            return this.itemStacks.get(slot);
        }

        public ItemStack[] getSlottedStacks() {
            return this.itemStacks.stream().map(ItemStack::copy).toArray(ItemStack[]::new);
        }

        public boolean setStackInSlot(int slot, @NotNull ItemStack stack) {
            if (!stack.isEmpty() && !this.canAdd(stack)) return false;

            this.itemStacks.set(slot, stack);
            if (!stack.isEmpty()) this.setItemType(stack);
            this.updateItemType();

            return true;
        }

        public ItemStack insertIntoSlot(int slot, @NotNull ItemStack stack, boolean simulate) {
            ItemStack refStack = stack.copy();
            if (!this.canAdd(stack)) return refStack;

            ItemStack currentItemType = this.getItemType();
            ItemStack itemType = currentItemType.isEmpty() ? refStack.copyWithCount(1) : currentItemType;
            ItemStack slotStack = this.getSlot(slot);

            int freeSpace = itemType.getMaxStackSize() - slotStack.getCount();
            int toAdd = Math.min(refStack.getCount(), freeSpace);
            if (toAdd <= 0) return refStack;

            if (!simulate) {
                this.setItemType(itemType);
                this.grow(toAdd);
            }

            refStack.shrink(toAdd);
            return refStack;
        }

        public @NotNull ItemStack extractFromSlot(int slot, int amount, boolean simulate) {
            ItemStack slotStack = this.getSlot(slot);
            if (slotStack.isEmpty()) return ItemStack.EMPTY;

            int finalAmt = Math.min(slotStack.getCount(), amount);
            ItemStack returnStack = slotStack.copyWithCount(finalAmt);

            if (!simulate) {
                if (finalAmt == slotStack.getCount()) this.itemStacks.set(slot, ItemStack.EMPTY);
                else slotStack.shrink(finalAmt);
                this.updateItemType();
            }

            return returnStack;
        }

        public @NotNull ItemStack removeFromSlot(int slot) {
            ItemStack slotStack = this.getSlot(slot);
            if (slotStack.isEmpty()) return ItemStack.EMPTY;

            this.setStackInSlot(slot, ItemStack.EMPTY);
            this.updateItemType();
            return slotStack;
        }
    }
}
