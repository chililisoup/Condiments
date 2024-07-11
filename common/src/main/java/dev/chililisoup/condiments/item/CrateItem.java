package dev.chililisoup.condiments.item;

import dev.chililisoup.condiments.item.Tooltip.CrateTooltip;
import net.minecraft.ChatFormatting;
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
        CompoundTag compoundTag = stack.getTagElement("BlockEntityTag");
        if (compoundTag == null) return -1;

        CompoundTag storageTag = compoundTag.getCompound("CrateItems").copy();

        float count = compoundTag.getCompound("CrateItems").getShort("Count");
        if (count <= 0 && !compoundTag.getBoolean("CrateLocked")) return -1;

        storageTag.putByte("Count", (byte) 1);
        return count / (ItemStack.of(storageTag).getMaxStackSize() * 64);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        CompoundTag compoundTag = stack.getTagElement("BlockEntityTag");

        if (compoundTag == null) {
            tooltip.add(Component.literal("Empty").withStyle(ChatFormatting.GRAY));
            return;
        }

        boolean locked = compoundTag.getBoolean("CrateLocked");
        if (locked)
            tooltip.add(Component.literal("Locked").withStyle(ChatFormatting.GRAY));

        CompoundTag storageTag = compoundTag.getCompound("CrateItems").copy();

        short count = compoundTag.getCompound("CrateItems").getShort("Count");
        if (count <= 0 && !locked) {
            tooltip.add(Component.literal("Empty").withStyle(ChatFormatting.GRAY));
            return;
        }

        storageTag.putByte("Count", (byte) 1);
        ItemStack itemStack = ItemStack.of(storageTag);
        tooltip.add(Component.literal(String.format("%d/%d", count, itemStack.getMaxStackSize() * 64)).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        CompoundTag compoundTag = stack.getTagElement("BlockEntityTag");

        if (compoundTag != null) {
            CompoundTag storageTag = compoundTag.getCompound("CrateItems").copy();
            storageTag.putByte("Count", (byte) 1);
            return Optional.of(new CrateTooltip(ItemStack.of(storageTag)));
        }

        return Optional.empty();
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack crateStack, Slot slot, ClickAction action, Player player) {
        if (crateStack.getCount() > 1) return false;
        if (action != ClickAction.SECONDARY) return false;

        ItemStack insertedStack = slot.getItem();
        if (insertedStack.isEmpty()) {
            this.playRemoveOneSound(player);
            removeOne(crateStack).ifPresent(stack ->
                add(crateStack, slot.safeInsert(stack))
            );
        } else if (canAdd(crateStack, insertedStack)) {
            int amt = add(crateStack, slot.safeTake(insertedStack.getCount(), getToAdd(crateStack, insertedStack), player));
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
            removeOne(crateStack).ifPresent((itemStack) -> {
                this.playRemoveOneSound(player);
                access.set(itemStack);
            });
        } else {
            int i = add(crateStack, insertedStack);
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
        CompoundTag compoundTag = crateStack.getTagElement("BlockEntityTag");
        if (compoundTag == null) return insertedStack.getCount();
        if (!compoundTag.contains("CrateItems")) return insertedStack.getCount();

        CompoundTag storageTag = compoundTag.getCompound("CrateItems");
        short count = storageTag.getShort("Count");
        int maxCount = insertedStack.getMaxStackSize() * 64;

        return Math.min(insertedStack.getCount(), (maxCount - count));
    }

    private static boolean canAdd(ItemStack crateStack, ItemStack insertedStack) {
        if (insertedStack.isEmpty()) return false;

        CompoundTag insertTag = insertedStack.getTagElement("BlockEntityTag");
        if (insertTag != null) {
            if (!insertTag.getList("Items", 10).isEmpty()) return false;
            if (insertTag.getCompound("CrateItems").getShort("Count") > 0) return false;
        }

        CompoundTag compoundTag = crateStack.getTagElement("BlockEntityTag");
        if (compoundTag == null) return true;
        if (!compoundTag.contains("CrateItems")) return true;

        CompoundTag storageTag = compoundTag.getCompound("CrateItems").copy();

        storageTag.putByte("Count", (byte) 1);
        ItemStack itemStack = ItemStack.of(storageTag);

        return itemStack.isEmpty() || ItemStack.isSameItemSameTags(itemStack, insertedStack);
    }

    private static int add(ItemStack crateStack, ItemStack insertedStack) {
        if (!canAdd(crateStack, insertedStack)) return 0;

        CompoundTag compoundTag = crateStack.getTagElement("BlockEntityTag");

        CompoundTag storageTag;
        if (compoundTag == null) {
            storageTag = new CompoundTag();
            crateStack.getOrCreateTag().put("BlockEntityTag", new CompoundTag());
        } else storageTag = compoundTag.getCompound("CrateItems");

        int amt = getToAdd(crateStack, insertedStack);
        short count = (short) (storageTag.getShort("Count") + amt);

        storageTag = insertedStack.save(new CompoundTag());
        storageTag.putShort("Count", count);

        crateStack.getOrCreateTag().getCompound("BlockEntityTag").put("CrateItems", storageTag);

        return amt;
    }

    private static Optional<ItemStack> removeOne(ItemStack stack) {
        CompoundTag compoundTag = stack.getTagElement("BlockEntityTag");
        if (compoundTag == null) return Optional.empty();
        if (!compoundTag.contains("CrateItems")) return Optional.empty();

        CompoundTag storageTag = compoundTag.getCompound("CrateItems");
        short count = storageTag.copy().getShort("Count");
        if (count <= 0) return Optional.empty();

        storageTag.putByte("Count", (byte) 1);
        ItemStack itemStack = ItemStack.of(storageTag);
        itemStack.setCount(Math.min(count, itemStack.getMaxStackSize()));

        storageTag.putShort("Count", (short) (count - itemStack.getCount()));
        if (!compoundTag.getBoolean("CrateLocked") && storageTag.getShort("Count") <= 0)
            stack.removeTagKey("BlockEntityTag");

        return Optional.of(itemStack);
    }

    private void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_FRAME_ADD_ITEM, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }
}
