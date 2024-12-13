package dev.chililisoup.condiments.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.chililisoup.condiments.reg.ModComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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

    public Optional<ItemStack> item() {
        return this.itemRecord.flatMap(itemRecord -> Optional.of(itemRecord.asItemStack()));
    }

    public boolean isLocked() {
        return this.locked.orElse(false);
    }

    public int capacity() {
        return this.item().map(
                stack -> stack.getMaxStackSize() * 64
        ).orElse(0);
    }

    public float fillPercent() {
        if (this.count <= 0 && !this.isLocked()) return -1;
        if (this.item().isEmpty()) return -1;
        return (float) this.count / this.capacity();
    }

    public String toString() {
        return String.format("%s x %d, %s", this.item(), this.count, this.isLocked() ? "LOCKED" : "UNLOCKED");
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

        private int getMaxAmountToAdd(ItemStack stack) {
            return this.item.map(
                    itemStack -> Math.max(itemStack.getMaxStackSize() * 64 - this.count, 0)
            ).orElseGet(() -> stack.getMaxStackSize() * 64);
        }

        public boolean canAdd(ItemStack stack) {
            if (stack.isEmpty()) return false;

            if (stack.has(ModComponents.CRATE_CONTENTS.get())) {
                CrateContents crateContents = stack.getOrDefault(ModComponents.CRATE_CONTENTS.get(), CrateContents.EMPTY);
                if (crateContents.count > 0) return false;
            }

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

        public Optional<ItemStack> removeOneStack() {
            if (this.item.isEmpty() || this.count <= 0) return Optional.empty();

            int amt = Math.min(this.item.get().getMaxStackSize(), this.count);
            this.count -= amt;

            Optional<ItemStack> returnStack = Optional.of(this.item.get().copyWithCount(amt));
            if (this.count <= 0 && !this.isLocked()) this.item = Optional.empty();
            return returnStack;
        }

        public CrateContents toImmutable() {
            return new CrateContents(this.item.flatMap(item -> Optional.of(ItemRecord.of(item))), this.count, this.locked);
        }

        public String toString() {
            return this.toImmutable().toString();
        }
    }
}
