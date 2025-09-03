package dev.chililisoup.condiments.block.entity;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.chililisoup.condiments.config.CommonConfig;
import dev.chililisoup.condiments.item.component.CrateContents;
import dev.chililisoup.condiments.reg.ModBlockEntities;
import dev.chililisoup.condiments.reg.ModComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CrateBlockEntity extends BlockEntity implements Container, Nameable {
    private ItemStack itemType;
    private NonNullList<ItemStack> items;
    private boolean locked = false;
    @Nullable
    private Component name;

    @Override
    public int getContainerSize() {
        return CommonConfig.CRATE_MAX_CONTAINED_STACKS.get();
    }

    protected CrateBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CRATE_BE_TYPE.get(), pos, blockState);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        this.itemType = ItemStack.EMPTY;
    }

    @ExpectPlatform
    public static CrateBlockEntity of(BlockPos pos, BlockState blockState) {
        throw new AssertionError();
    }

    public int getCount() {
        int saveCount = 0;
        for (ItemStack item : this.items) saveCount += item.getCount();
        return saveCount;
    }

    public ItemStack getItemType() {
        return this.itemType.copy();
    }

    public boolean isLocked() {
        return this.locked;
    }

    private CompoundTag prepareUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag storageTag = (CompoundTag) this.findFirst().saveOptional(registries);
        storageTag.putShort("count", (short) getCount());

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
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        prepareUpdateTag(tag, registries);
        if (this.name != null) {
            tag.putString("CustomName", Component.Serializer.toJson(this.name, registries));
        }
    }

    private void loadStorage(CompoundTag tag, HolderLookup.Provider registries) {
        this.locked = tag.getBoolean("CrateLocked");

        short count = tag.getCompound("CrateItems").getShort("count");
        CompoundTag storageTag = tag.getCompound("CrateItems");

        if (storageTag.contains("id")) storageTag.putInt("count", 1);
        else storageTag.remove("count");

        this.itemType = ItemStack.parseOptional(registries, storageTag);
        this.itemType.setCount(1);

        this.populateItems(count);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        loadStorage(tag, registries);
        if (tag.contains("CustomName", 8)) {
            this.name = parseCustomNameSafe(tag.getString("CustomName"), registries);
        }
    }

    public ItemStack findFirst() {
        if (this.locked && !this.itemType.is(Items.AIR)) return this.itemType;

        for (ItemStack item : this.items) {
            if (item.isEmpty()) continue;
            return item;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if (CrateContents.isItemUnsafe(stack)) return false;

        return (this.isEmpty() || ItemStack.isSameItemSameComponents(this.findFirst(), stack));
    }

    @Override
    public boolean isEmpty() {
        return this.findFirst().isEmpty();
    }

    private void updateItemType() {
        if (this.shouldSaveItemType()) return;
        this.itemType = ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return this.getItems().get(slot);
    }

    @Override
    public int getMaxStackSize() {
        return this.itemType.getMaxStackSize();
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        ItemStack itemStack = ContainerHelper.removeItem(this.getItems(), slot, amount);
        if (!itemStack.isEmpty()) this.setChanged();
        this.updateItemType();
        updateClient();
        return itemStack;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = ContainerHelper.takeItem(this.getItems(), slot);
        this.updateItemType();
        updateClient();
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (!stack.isEmpty()) this.itemType = stack.copyWithCount(1);

        this.getItems().set(slot, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        this.setChanged();
        if (stack.isEmpty()) this.updateItemType();
        updateClient();
    }

    private void populateItems(int count) {
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        int max = this.getMaxStackSize();

        for (int i = 0; i < this.items.size(); i++) {
            int rem = count - (i * max);
            int amt = Math.min(rem, max);
            ItemStack stack = this.itemType.copyWithCount(amt);
            this.items.set(i, stack);
            if (rem < max) break;
        }
    }

    private void addAllInventory(Player player) {
        ItemStack first = this.findFirst().copy();
        if (first.isEmpty()) return;

        int max = this.getMaxStackSize();

        int addCount = player.getInventory().clearOrCountMatchingItems(
                item -> ItemStack.isSameItemSameComponents(first, item),
                (max * CommonConfig.CRATE_MAX_CONTAINED_STACKS.get()) - getCount(),
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

    public ItemStack tryAddStack(ItemStack stack, boolean simulate) {
        if (!canPlaceItem(0, stack)) return stack;
        if (stack.isEmpty()) return ItemStack.EMPTY;

        int max = stack.getMaxStackSize();
        this.itemType = stack.copyWithCount(1);

        for (int i = 0; i < this.items.size(); i++) {
            ItemStack item = this.items.get(i);
            if (item.getCount() >= max) continue;

            int take = Math.min(stack.getCount(), max - item.getCount());

            if (!simulate) this.items.set(i, stack.copyWithCount(item.getCount() + take));
            stack.setCount(stack.getCount() - take);

            if (stack.isEmpty()) {
                stack = ItemStack.EMPTY;
                break;
            }
        }

        this.setChanged();
        playSound(this.getBlockState(), SoundEvents.ITEM_FRAME_ADD_ITEM);
        updateClient();
        return stack;
    }

    public ItemStack tryAddStack(ItemStack stack) {
        return this.tryAddStack(stack, false);
    }

    public ItemStack tryAddStack(ItemStack stack, Player player) {
        if (stack.isEmpty()) {
            addAllInventory(player);
            return ItemStack.EMPTY;
        }

        return this.tryAddStack(stack);
    }

    public ItemStack request(int amount) {
        if (this.isEmpty()) return ItemStack.EMPTY;
        ItemStack base = this.findFirst().copyWithCount(1);
        amount = amount < 0 ?
                this.getMaxStackSize() :
                Math.min(this.getMaxStackSize(), amount);

        int fulfilled = 0;
        for (ItemStack item : this.items) {
            if (item.isEmpty()) continue;

            int take = Math.min(item.getCount(), amount);

            item.setCount(item.getCount() - take);
            amount -= take;
            fulfilled += take;

            if (amount <= 0) break;
        }

        if (fulfilled <= 0) return ItemStack.EMPTY;

        base.setCount(fulfilled);
        this.setChanged();
        this.updateItemType();
        updateClient();
        playSound(this.getBlockState(), SoundEvents.ITEM_FRAME_REMOVE_ITEM);
        return base;
    }

    public ItemStack request(boolean fullStack) {
        return this.request(fullStack ? -1 : 1);
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
        this.updateItemType();
        updateClient();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return prepareUpdateTag(new CompoundTag(), registries);
    }

    void playSound(BlockState state, SoundEvent sound) {
        if (level == null) return;
        Vec3i vec3i = state.getValue(BlockStateProperties.ORIENTATION).front().getNormal();
        double d = (double)this.worldPosition.getX() + 0.5 + (double)vec3i.getX() / 2.0;
        double e = (double)this.worldPosition.getY() + 0.5 + (double)vec3i.getY() / 2.0;
        double f = (double)this.worldPosition.getZ() + 0.5 + (double)vec3i.getZ() / 2.0;
        this.level.playSound(null, d, e, f, sound, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
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

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.name = componentInput.get(DataComponents.CUSTOM_NAME);

        CrateContents crateContents = componentInput.getOrDefault(ModComponents.CRATE_CONTENTS.get(), CrateContents.EMPTY);
        this.loadCrateContents(crateContents);
    }

    public void loadCrateContents(CrateContents crateContents) {
        this.locked = crateContents.isLocked();
        this.itemType = crateContents.item().orElse(ItemStack.EMPTY);
        this.populateItems(crateContents.count());
    }

    public boolean shouldSaveItemType() {
        if (this.isEmpty()) return this.locked && !this.itemType.isEmpty();
        return true;
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.CUSTOM_NAME, this.name);

        int count = this.getCount();
        components.set(ModComponents.CRATE_CONTENTS.get(), new CrateContents(
                this.shouldSaveItemType() ? Optional.of(CrateContents.ItemRecord.of(this.itemType)) : Optional.empty(),
                count,
                this.locked ? Optional.of(true) : Optional.empty()
        ));

        components.set(DataComponents.MAX_STACK_SIZE, count > 0 ? 1 : CommonConfig.EMPTY_CRATE_STACK_SIZE.get());
    }
}
