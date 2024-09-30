package dev.chililisoup.condiments.item;

import dev.chililisoup.condiments.item.tooltip.CrateTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class CrateItem extends BlockItem {
    private static final int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);

    public CrateItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getFillPercent(stack) >= 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        float fillPercent = getFillPercent(stack);
        return Math.min((fillPercent > 0 ? 1 : 0) + (int) Math.floor(fillPercent * 12), 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return BAR_COLOR;
    }

    private static float getFillPercent(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (customData == null) return -1;

        CompoundTag compoundTag = customData.copyTag();
        CompoundTag storageTag = compoundTag.getCompound("CrateItems").copy();

        float count = compoundTag.getCompound("CrateItems").getShort("Count");
        if (count <= 0 && !compoundTag.getBoolean("CrateLocked")) return -1;

        storageTag.putByte("Count", (byte) 1);
        return count / (ItemStack.parseOptional(RegistryAccess.EMPTY, storageTag).getMaxStackSize() * 64);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        CustomData customData = stack.get(DataComponents.BLOCK_ENTITY_DATA);

        if (customData == null) {
            tooltipComponents.add(Component.literal("Empty").withStyle(ChatFormatting.GRAY));
            return;
        }

        CompoundTag compoundTag = customData.copyTag();
        boolean locked = compoundTag.getBoolean("CrateLocked");
        if (locked)
            tooltipComponents.add(Component.literal("Locked").withStyle(ChatFormatting.GRAY));

        CompoundTag storageTag = compoundTag.getCompound("CrateItems").copy();

        short count = compoundTag.getCompound("CrateItems").getShort("Count");
        if (count <= 0 && !locked) {
            tooltipComponents.add(Component.literal("Empty").withStyle(ChatFormatting.GRAY));
            return;
        }

        storageTag.putByte("Count", (byte) 1);
        ItemStack itemStack = ItemStack.parseOptional(RegistryAccess.EMPTY, storageTag);
        tooltipComponents.add(Component.literal(String.format("%d/%d", count, itemStack.getMaxStackSize() * 64)).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (customData == null) return Optional.empty();

        CompoundTag storageTag = customData.copyTag().getCompound("CrateItems").copy();
        storageTag.putByte("Count", (byte) 1);
        return Optional.of(new CrateTooltip(ItemStack.parseOptional(RegistryAccess.EMPTY, storageTag)));
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack crateStack, Slot slot, ClickAction action, Player player) {
        if (crateStack.getCount() > 1) return false;
        if (action != ClickAction.SECONDARY) return false;

        ItemStack insertedStack = slot.getItem();
        if (insertedStack.isEmpty()) {
            this.playRemoveOneSound(player);
            removeOne(crateStack, player.level()).ifPresent(stack ->
                add(crateStack, slot.safeInsert(stack), player.level())
            );
        } else if (canAdd(crateStack, insertedStack, player.level())) {
            int amt = add(crateStack, slot.safeTake(insertedStack.getCount(), getToAdd(crateStack, insertedStack), player), player.level());
            if (amt > 0) this.playInsertSound(player);
        }

        slot.setChanged();
        player.containerMenu.slotsChanged(slot.container);
        player.inventoryMenu.slotsChanged(slot.container);

        return true;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack crateStack, ItemStack insertedStack, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (crateStack.getCount() > 1) return false;
        if (action != ClickAction.SECONDARY || !slot.allowModification(player)) return false;

        if (insertedStack.isEmpty()) {
            removeOne(crateStack, player.level()).ifPresent((itemStack) -> {
                this.playRemoveOneSound(player);
                access.set(itemStack);
            });
        } else {
            int i = add(crateStack, insertedStack, player.level());
            if (i > 0) {
                this.playInsertSound(player);
                insertedStack.shrink(i);
            }
        }

        slot.setChanged();
        player.containerMenu.slotsChanged(slot.container);
        player.inventoryMenu.slotsChanged(slot.container);

        return true;
    }

    private static int getToAdd(ItemStack crateStack, ItemStack insertedStack) {
        CustomData customData = crateStack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (customData == null) return insertedStack.getCount();

        CompoundTag compoundTag = customData.copyTag();
        if (!compoundTag.contains("CrateItems")) return insertedStack.getCount();

        CompoundTag storageTag = compoundTag.getCompound("CrateItems");
        short count = storageTag.getShort("Count");
        int maxCount = insertedStack.getMaxStackSize() * 64;

        return Math.min(insertedStack.getCount(), (maxCount - count));
    }

    private static boolean canAdd(ItemStack crateStack, ItemStack insertedStack, Level level) {
        if (insertedStack.isEmpty()) return false;

        CustomData insertData = insertedStack.get(DataComponents.BLOCK_ENTITY_DATA);

        if (insertData != null) {
            CompoundTag insertTag = insertData.copyTag();

            if (!insertTag.getList("Items", 10).isEmpty()) return false;
            if (insertTag.getCompound("CrateItems").getShort("Count") > 0) return false;
        }

        CustomData customData = crateStack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (customData == null) return true;

        CompoundTag compoundTag = customData.copyTag();
        if (!compoundTag.contains("CrateItems")) return true;

        CompoundTag storageTag = compoundTag.getCompound("CrateItems").copy();

        storageTag.putByte("Count", (byte) 1);
        ItemStack itemStack = ItemStack.parseOptional(level.registryAccess(), storageTag);

        return itemStack.isEmpty() || ItemStack.isSameItemSameComponents(itemStack, insertedStack);
    }

    private static int add(ItemStack crateStack, ItemStack insertedStack, Level level) {
        if (!canAdd(crateStack, insertedStack, level)) return 0;

        CustomData customData = crateStack.get(DataComponents.BLOCK_ENTITY_DATA);

        CompoundTag compoundTag;
        CompoundTag storageTag;

        if (customData == null) {
            compoundTag = new CompoundTag();
            storageTag = new CompoundTag();
        } else {
            compoundTag = customData.copyTag();
            storageTag = compoundTag.getCompound("CrateItems");
        }

        int amt = getToAdd(crateStack, insertedStack);
        short count = (short) (storageTag.getShort("Count") + amt);

        storageTag = (CompoundTag) insertedStack.saveOptional(level.registryAccess());
        storageTag.putShort("Count", count);

        compoundTag.put("CrateItems", storageTag);
        crateStack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(compoundTag));

        return amt;
    }

    private static Optional<ItemStack> removeOne(ItemStack stack, Level level) {
        CustomData customData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (customData == null) return Optional.empty();

        CompoundTag compoundTag = customData.copyTag();
        if (!compoundTag.contains("CrateItems")) return Optional.empty();

        CompoundTag storageTag = compoundTag.getCompound("CrateItems");
        short count = storageTag.copy().getShort("Count");
        if (count <= 0) return Optional.empty();

        storageTag.putByte("Count", (byte) 1);
        ItemStack itemStack = ItemStack.parseOptional(level.registryAccess(), storageTag);
        itemStack.setCount(Math.min(count, itemStack.getMaxStackSize()));

        storageTag.putShort("Count", (short) (count - itemStack.getCount()));
        if (!compoundTag.getBoolean("CrateLocked") && storageTag.getShort("Count") <= 0)
            stack.remove(DataComponents.BLOCK_ENTITY_DATA);

        return Optional.of(itemStack);
    }

    private void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_FRAME_ADD_ITEM, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }
}
