package dev.chililisoup.condiments.block.entity;

import dev.chililisoup.condiments.reg.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrateBlockEntity extends BlockEntity implements Container, Nameable {
    private static final ItemStack EMPTY;

    private ItemStack itemType;
    private NonNullList<ItemStack> items;
    private boolean locked = false;
    @Nullable
    private Component name;

    @Override
    public int getContainerSize() {
        return 64;
    }

    public CrateBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CRATE_BE_TYPE.get(), pos, blockState);
        this.items = NonNullList.withSize(this.getContainerSize(), EMPTY);
    }

    public int getCount() {
        int saveCount = 0;
        for (ItemStack item : this.items) saveCount += item.getCount();
        return saveCount;
    }

    private CompoundTag prepareUpdateTag(CompoundTag tag) {
        CompoundTag storageTag = this.findFirst().save(new CompoundTag());
        storageTag.putShort("Count", (short) getCount());

        tag.put("CrateItems", storageTag);
        tag.putBoolean("CrateLocked", this.locked);

        return tag;
    }

    private void updateClient() {
        if (level != null) {
            level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        prepareUpdateTag(tag);
        if (this.name != null) {
            tag.putString("CustomName", Component.Serializer.toJson(this.name));
        }
    }

    private void loadStorage(CompoundTag tag) {
        this.items = NonNullList.withSize(this.getContainerSize(), EMPTY);
        this.locked = tag.getBoolean("CrateLocked");

        short count = tag.getCompound("CrateItems").getShort("Count");
        CompoundTag storageTag = tag.getCompound("CrateItems");

        storageTag.putByte("Count", (byte) 1);
        this.itemType = ItemStack.of(storageTag);
        this.itemType.setCount(1);
        int max = ItemStack.of(storageTag).getMaxStackSize();

        for (int i = 0; i < this.items.size(); i++) {
            int rem = count - (i * max);
            int amt = Math.min(rem, max);
            ItemStack stack = ItemStack.of(storageTag);
            stack.setCount(amt);
            this.items.set(i, stack);
            if (rem < max) break;
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        loadStorage(tag);
        if (tag.contains("CustomName", 8)) {
            this.name = Component.Serializer.fromJson(tag.getString("CustomName"));
        }
    }

    public ItemStack findFirst() {
        if (this.locked && !itemType.is(Items.AIR)) return itemType;

        for (ItemStack item : this.items) {
            if (item.isEmpty()) continue;
            return item;
        }
        return EMPTY;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        CompoundTag tag = stack.getTagElement("BlockEntityTag");
        if (tag != null) {
            if (!tag.getList("Items", 10).isEmpty()) return false;
            if (tag.getCompound("CrateItems").getShort("Count") > 0) return false;
        }
        return (this.isEmpty() || ItemStack.isSameItemSameTags(this.findFirst(), stack));
    }

    @Override
    public boolean isEmpty() {
        return this.findFirst() == EMPTY;
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return this.getItems().get(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        ItemStack itemStack = ContainerHelper.removeItem(this.getItems(), slot, amount);
        if (!itemStack.isEmpty()) this.setChanged();
        updateClient();
        return itemStack;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = ContainerHelper.takeItem(this.getItems(), slot);
        updateClient();
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.itemType = stack.copyWithCount(1);
        this.getItems().set(slot, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        this.setChanged();
        updateClient();
    }

    private void addAllInventory(Player player) {
        ItemStack first = this.findFirst().copy();
        if (first.isEmpty()) return;

        int max = first.getMaxStackSize();

        int addCount = player.getInventory().clearOrCountMatchingItems(
                item -> ItemStack.isSameItemSameTags(first, item),
                (max * 64) - getCount(),
                player.getInventory()
        );

        if (addCount <= 0) return;

        for (int i = 0; i < this.items.size(); i++) {
            ItemStack item = this.items.get(i);
            if (item.getCount() >= max) continue;

            int take = Math.min(addCount, max - item.getCount());

            this.items.set(i, first.copyWithCount(item.getCount() + take));
            addCount -= take;

            if (addCount <= 0) break;
        }

        this.setChanged();
        playSound(this.getBlockState(), SoundEvents.ITEM_FRAME_ADD_ITEM);
        updateClient();
    }

    public ItemStack tryAddStack(ItemStack stack, Player player) {
        if (stack.isEmpty()) {
            addAllInventory(player);
            return EMPTY;
        }

        if (!canPlaceItem(0, stack)) return stack;

        int max = stack.getMaxStackSize();
        this.itemType = stack.copyWithCount(1);

        for (int i = 0; i < this.items.size(); i++) {
            ItemStack item = this.items.get(i);
            if (item.getCount() >= max) continue;

            int take = Math.min(stack.getCount(), max - item.getCount());

            this.items.set(i, stack.copyWithCount(item.getCount() + take));
            stack.setCount(stack.getCount() - take);

            if (stack.isEmpty()) {
                stack = EMPTY;
                break;
            }
        }

        this.setChanged();
        playSound(this.getBlockState(), SoundEvents.ITEM_FRAME_ADD_ITEM);
        updateClient();
        return stack;
    }

    public ItemStack request(boolean fullStack) {
        if (this.isEmpty()) return EMPTY;
        ItemStack base = this.findFirst().copyWithCount(1);

        int fulfilled = 0;
        int amount = fullStack ? base.getMaxStackSize() : 1;
        for (ItemStack item : this.items) {
            if (item.isEmpty()) continue;

            int take = Math.min(item.getCount(), amount);

            item.setCount(item.getCount() - take);
            amount -= take;
            fulfilled += take;

            if (amount <= 0) break;
        }

        if (fulfilled <= 0) return EMPTY;

        base.setCount(fulfilled);
        this.setChanged();
        updateClient();
        playSound(this.getBlockState(), SoundEvents.ITEM_FRAME_REMOVE_ITEM);
        return base;
    }

    protected @NotNull NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.getItems().clear();
        updateClient();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return prepareUpdateTag(new CompoundTag());
    }

    void playSound(BlockState state, SoundEvent sound) {
        if (level == null) return;
        Vec3i vec3i = state.getValue(BarrelBlock.FACING).getNormal();
        double d = (double)this.worldPosition.getX() + 0.5 + (double)vec3i.getX() / 2.0;
        double e = (double)this.worldPosition.getY() + 0.5 + (double)vec3i.getY() / 2.0;
        double f = (double)this.worldPosition.getZ() + 0.5 + (double)vec3i.getZ() / 2.0;
        this.level.playSound(null, d, e, f, sound, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
    }

    public void setCustomName(Component name) {
        this.name = name;
    }

    protected Component getDefaultName() {
        return Component.translatable("container.condiments.crate");
    }

    @Override
    public @NotNull Component getName() {
        return this.name != null ? this.name : this.getDefaultName();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return this.getName();
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return this.name;
    }
    
    
    
    static {
        CompoundTag emptyTag = new CompoundTag();
        emptyTag.putString("id", "minecraft:air");
        emptyTag.putByte("Count", (byte) 0);
        EMPTY = ItemStack.of(emptyTag);
    }
}
