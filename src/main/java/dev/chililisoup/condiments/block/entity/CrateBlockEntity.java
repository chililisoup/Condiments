package dev.chililisoup.condiments.block.entity;

import dev.chililisoup.condiments.extra.VersionHelper;
import dev.chililisoup.condiments.reg.ModBlockEntities;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//? if >= 1.21 {
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.HolderLookup;
import dev.chililisoup.condiments.reg.ModComponents;
import dev.chililisoup.condiments.config.CommonConfig;
//?}

//? if forgeLike
/*import dev.chililisoup.condiments.item.CrateItemHandler;*/

import java.util.function.Predicate;

//? if forgeLike
/*@javax.annotation.ParametersAreNonnullByDefault*/
public class CrateBlockEntity extends BlockEntity implements Container, Nameable {
    //? if < 1.21 {
    /*public static final String COUNT_KEY = "Count";
    *///?} else
    public static final String COUNT_KEY = "count";

    private final CrateContents.SlottedMutable contents;
    private @Nullable Component name;

    //? if forgeLike
    /*public final CrateItemHandler handler;*/

    @Override
    public int getContainerSize() {
        return CrateContents.maxStacks();
    }

    public CrateBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CRATE_BE_TYPE.get(), pos, blockState);
        this.contents = new CrateContents().toSlottedMutable();

        //? if forgeLike
        /*this.handler = new CrateItemHandler(this);*/
    }

    public CrateContents getContents() {
        return this.contents.toImmutable();
    }

    public ItemStack getItemType() {
        return this.contents.getItemType();
    }

    public int getCount() {
        return this.contents.getCount();
    }

    public boolean isLocked() {
        return this.contents.isLocked();
    }

    private CompoundTag prepareUpdateTag(
            CompoundTag tag
            //? if >= 1.21
            , HolderLookup.Provider registries
    ) {
        //? if < 1.21 {
        /*CompoundTag storageTag = new CompoundTag();
        ItemStack itemType = this.getItemType();
        if (!itemType.isEmpty()) itemType.save(storageTag);
        *///?} else {
        CompoundTag storageTag = (CompoundTag) this.getItemType().saveOptional(registries);
        //?}

        storageTag.putShort(COUNT_KEY, (short) this.getCount());

        tag.put("CrateItems", storageTag);
        tag.putBoolean("CrateLocked", this.isLocked());
        if (this.name != null) tag.putString(
                "CustomName",
                //? if < 1.21 {
                /*Component.Serializer.toJson(this.name)
                *///?} else
                Component.Serializer.toJson(this.name, registries)
        );

        return tag;
    }

    @Override
    //? if < 1.21 {
    /*protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        prepareUpdateTag(tag);
    }
    *///?} else {
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        prepareUpdateTag(tag, registries);
    }
    //?}

    private void loadStorage(
            CompoundTag tag
            //? if >= 1.21
            , HolderLookup.Provider registries
    ) {
        this.contents.setLocked(tag.getBoolean("CrateLocked"));

        short count = tag.getCompound("CrateItems").getShort(COUNT_KEY);
        CompoundTag storageTag = tag.getCompound("CrateItems");

        if (storageTag.contains("id")) storageTag.putInt(COUNT_KEY, 1);
        else storageTag.remove(COUNT_KEY);

        //? if < 1.21 {
        /*this.contents.setItemType(storageTag.isEmpty() ? null : ItemStack.of(storageTag));
        *///?} else
        this.contents.setItemType(ItemStack.parseOptional(registries, storageTag));
        this.contents.setCount(count);

        if (tag.contains("CustomName", 8)) {
            //? if < 1.21 {
            /*this.name = Component.Serializer.fromJson(tag.getString("CustomName"));
            *///?} else
            this.name = parseCustomNameSafe(tag.getString("CustomName"), registries);
        } else this.name = null;
    }

    @Override
    //? if < 1.21 {
    /*public void load(CompoundTag tag) {
        super.load(tag);
        loadStorage(tag);
    }
    *///?} else {
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        loadStorage(tag, registries);
    }
    //?}

    private void updateClient() {
        if (level != null)
            level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_CLIENTS);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return this.contents.canAdd(stack);
    }

    @Override
    public int countItem(Item item) {
        return this.getItemType().getItem().equals(item) ?
                this.getCount() :
                0;
    }

    @Override
    public boolean hasAnyMatching(Predicate<ItemStack> predicate) {
        return predicate.test(this.getItemType());
    }

    @Override
    public boolean isEmpty() {
        return this.contents.isEmptyNoLock();
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return this.contents.getSlot(slot);
    }

    @Override
    public int getMaxStackSize() {
        return this.contents.getMaxStackSize();
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        ItemStack item = this.contents.extractFromSlot(slot, amount, false);
        if (item.isEmpty()) return item;

        this.setChanged();
        return item;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        return this.contents.removeFromSlot(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (!this.contents.setStackInSlot(slot, stack)) return;

        this.setChanged();
    }

    private void addAllInventory(Player player) {
        if (!this.contents.addAllInventory(player)) return;

        this.setChanged();
        playSound(this.getBlockState(), SoundEvents.ITEM_FRAME_ADD_ITEM);
    }

    public ItemStack tryAddStack(ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return stack;
        ItemStack item = this.contents.tryAddStack(stack, simulate);
        if (VersionHelper.itemsMatch(item, stack) && item.getCount() == stack.getCount())
            return item;

        this.setChanged();
        playSound(this.getBlockState(), SoundEvents.ITEM_FRAME_ADD_ITEM);
        return item;
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

    public ItemStack insertIntoSlot(int slot, ItemStack stack, boolean simulate) {
        ItemStack result = this.contents.insertIntoSlot(slot, stack, simulate);
        this.setChanged();
        return result;
    }

    public ItemStack extractFromSlot(int slot, int amount, boolean simulate) {
        ItemStack result = this.contents.extractFromSlot(slot, amount, simulate);
        this.setChanged();
        return result;
    }

    private ItemStack finishRequest(ItemStack stack) {
        this.setChanged();
        playSound(this.getBlockState(), SoundEvents.ITEM_FRAME_REMOVE_ITEM);

        return stack;
    }

    public ItemStack request(int amount) {
        return this.finishRequest(this.contents.request(amount));
    }

    public ItemStack requestOne() {
        return this.finishRequest(this.contents.requestOne());
    }

    public ItemStack requestOneStack() {
        return this.finishRequest(this.contents.requestOneStack());
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.contents.clear();
        this.setChanged();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        updateClient();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    //? if < 1.21 {
    /*public @NotNull CompoundTag getUpdateTag() {
        return prepareUpdateTag(new CompoundTag());
    }
    *///?} else {
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return prepareUpdateTag(new CompoundTag(), registries);
    }
    //?}

    void playSound(BlockState state, SoundEvent sound) {
        if (level == null) return;
        Vec3i front = state.getValue(BlockStateProperties.ORIENTATION).front().getNormal();
        Vec3 pos = this.worldPosition.getCenter().add(
            (double) front.getX() / 2.0,
            (double) front.getY() / 2.0,
            (double) front.getZ() / 2.0
        );

        this.level.playSound(
                null,
                pos.x,
                pos.y,
                pos.z,
                sound,
                SoundSource.BLOCKS,
                0.5F,
                this.level.random.nextFloat() * 0.1F + 0.9F
        );
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

    //? if < 1.21 {
    /*public void setCustomName(Component name) {
        this.name = name;
    }
    *///?} else {
    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.name = componentInput.get(DataComponents.CUSTOM_NAME);

        CrateContents crateContents = componentInput.getOrDefault(ModComponents.CRATE_CONTENTS.get(), CrateContents.EMPTY);
        this.loadCrateContents(crateContents);
    }

    public void loadCrateContents(CrateContents crateContents) {
        this.contents.setValues(crateContents);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.CUSTOM_NAME, this.name);
        components.set(ModComponents.CRATE_CONTENTS.get(), this.contents.toImmutable());
        components.set(DataComponents.MAX_STACK_SIZE, this.getCount() > 0 ? 1 : CommonConfig.EMPTY_CRATE_STACK_SIZE.get());
    }
    //?}
}
