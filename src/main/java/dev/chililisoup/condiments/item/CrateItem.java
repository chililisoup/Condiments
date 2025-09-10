package dev.chililisoup.condiments.item;

import dev.chililisoup.condiments.config.CommonConfig;
import dev.chililisoup.condiments.block.entity.CrateContents;
import dev.chililisoup.condiments.item.tooltip.CrateTooltip;
import dev.chililisoup.condiments.mixin.BlockItemAccess;
import dev.chililisoup.condiments.mixin.UseOnContextAccess;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

//? if < 1.21 {
/*import dev.chililisoup.condiments.client.renderer.CrateItemRenderer;
import net.mehvahdjukaar.moonlight.api.client.ItemStackRenderer;
import net.mehvahdjukaar.moonlight.api.client.ICustomItemRendererProvider;

import java.util.function.Supplier;
*///?}

import java.util.List;
import java.util.Optional;

//? if forgeLike
/*@javax.annotation.ParametersAreNonnullByDefault*/
public class CrateItem extends BlockItem
//? if < 1.21
/*implements ICustomItemRendererProvider*/
{
    private static final int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);

    public CrateItem(Block block, Properties properties) {
        super(block, properties.stacksTo(CommonConfig.EMPTY_CRATE_STACK_SIZE.get()));
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        CrateContents crateContents = CrateContents.fromCrateItem(stack);

        return crateContents.fillPercent() >= 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        CrateContents crateContents = CrateContents.fromCrateItem(stack);

        float fillPercent = crateContents.fillPercent();
        return Math.min((fillPercent > 0 ? 1 : 0) + (int) Math.floor(fillPercent * 12), 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return BAR_COLOR;
    }

    @Override
    //? if < 1.21 {
    /*public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
    *///?} else {
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    //?}
        CrateContents crateContents = CrateContents.fromCrateItem(stack);

        if (crateContents.isLocked()) {
            tooltipComponents.add(Component.literal(crateContents.item().isEmpty() ? "Locked - Unset" : "Locked").withStyle(ChatFormatting.GRAY));
        }

        if (crateContents.item().isEmpty()) {
            tooltipComponents.add(Component.literal("Empty").withStyle(ChatFormatting.GRAY));
            return;
        }

        if (crateContents.count() <= 0 && !crateContents.isLocked()) {
            tooltipComponents.add(Component.literal("Empty").withStyle(ChatFormatting.GRAY));
            return;
        }

        tooltipComponents.add(Component.literal(String.format("%d/%d", crateContents.count(), crateContents.capacity())).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        CrateContents crateContents = CrateContents.fromCrateItem(stack);

        return crateContents.item().flatMap(item -> Optional.of(new CrateTooltip(item)));
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack crateStack, Slot slot, ClickAction action, Player player) {
        if (crateStack.getCount() > 1) return false;
        if (action != ClickAction.SECONDARY) return false;

        CrateContents crateContents = CrateContents.fromCrateItem(crateStack);
        CrateContents.Mutable mutable = crateContents.toMutable();

        ItemStack insertedStack = slot.getItem();
        if (insertedStack.isEmpty()) {
            this.playRemoveOneSound(player);
            slot.safeInsert(mutable.requestOneStack());
        } else {
            int amt = mutable.getToAdd(insertedStack);
            if (amt > 0) {
                this.playInsertSound(player);
                mutable.addFromStack(insertedStack, amt);
            }
        }

        mutable.toImmutable().updateCrateItem(crateStack);

        slot.setChanged();
        player.containerMenu.slotsChanged(slot.container);
        player.inventoryMenu.slotsChanged(slot.container);

        return true;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack crateStack, ItemStack insertedStack, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (crateStack.getCount() > 1) return false;
        if (action != ClickAction.SECONDARY || !slot.allowModification(player)) return false;

        CrateContents crateContents = CrateContents.fromCrateItem(crateStack);
        CrateContents.Mutable mutable = crateContents.toMutable();

        if (insertedStack.isEmpty()) {
            ItemStack itemStack = mutable.requestOneStack();
            if (!itemStack.isEmpty()) {
                this.playRemoveOneSound(player);
                access.set(itemStack);
            }
        } else {
            int amt = mutable.getToAdd(insertedStack);
            if (amt > 0) {
                this.playInsertSound(player);
                mutable.addFromStack(insertedStack, amt);
            }
        }

        mutable.toImmutable().updateCrateItem(crateStack);

        slot.setChanged();
        player.containerMenu.slotsChanged(slot.container);
        player.inventoryMenu.slotsChanged(slot.container);

        return true;
    }

    private void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_FRAME_ADD_ITEM, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private InteractionResult placeContents(BlockPlaceContext context, BlockItem blockItem, ItemStack itemStack) {
        if (!blockItem.getBlock().isEnabled(context.getLevel().enabledFeatures()) || !context.canPlace())
            return InteractionResult.FAIL;

        BlockPlaceContext blockPlaceContext = blockItem.updatePlacementContext(context);
        if (blockPlaceContext == null)
            return InteractionResult.FAIL;

        BlockState blockState = ((BlockItemAccess) blockItem).condiments$getPlacementState(blockPlaceContext);
        if (blockState == null || !this.placeBlock(blockPlaceContext, blockState))
            return InteractionResult.FAIL;

        BlockPos blockPos = blockPlaceContext.getClickedPos();
        Level level = blockPlaceContext.getLevel();
        Player player = blockPlaceContext.getPlayer();
        BlockState clickedState = level.getBlockState(blockPos);

        // Make sure to not ignore stored block nbt
        if (clickedState.is(blockState.getBlock())) {
            clickedState = ((BlockItemAccess) blockItem).condiments$updateBlockStateFromTag(blockPos, level, itemStack, clickedState);

            ((BlockItemAccess) blockItem).condiments$updateCustomBlockEntityTag(blockPos, level, player, itemStack, clickedState);
            //? if >= 1.21
            BlockItemAccess.condiments$updateBlockEntityComponents(level, blockPos, itemStack);
            clickedState.getBlock().setPlacedBy(level, blockPos, clickedState, player, itemStack);

            if (player instanceof ServerPlayer serverPlayer)
                CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, blockPos, itemStack);
        }

        SoundType soundType = clickedState.getSoundType();
        level.playSound(player, blockPos, ((BlockItemAccess) blockItem).condiments$getPlaceSound(clickedState), SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
        level.gameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Context.of(player, clickedState));

        //? if < 1.21 {
        /*if (player == null || !player.getAbilities().instabuild) itemStack.shrink(1);
        *///?} else
        itemStack.consume(1, player);

        return InteractionResult.sidedSuccess(level.isClientSide);

    }

    @Override
    public @NotNull InteractionResult place(BlockPlaceContext context) {
        Player player = context.getPlayer();
        ItemStack crateStack = context.getItemInHand();
        if (player == null || player.isShiftKeyDown() || !(crateStack.getItem() instanceof CrateItem))
            return super.place(context);

        CrateContents crateContents = CrateContents.fromCrateItem(crateStack);
        if (crateContents.count() <= 0)
            return super.place(context);

        CrateContents.ItemRecord itemRecord = crateContents.itemRecord();
        if (itemRecord == null)
            return super.place(context);

        ItemStack contentsStack = itemRecord.asItemStack();
        if (contentsStack.getItem() instanceof BlockItem blockItem) {
            BlockPlaceContext contentsContext = new BlockPlaceContext(
                    player,
                    context.getHand(),
                    contentsStack,
                    ((UseOnContextAccess) context).condiments$getHitResult()
            );
            InteractionResult result = this.placeContents(contentsContext, blockItem, contentsStack);

            //? if < 1.21 {
            /*if (!player.getAbilities().instabuild && result.shouldAwardStats()) {
            *///?} else
            if (!player.hasInfiniteMaterials() && result.indicateItemUse()) {
                CrateContents.Mutable mutable = crateContents.toMutable();
                mutable.requestOne();

                mutable.toImmutable().updateCrateItem(crateStack);
            }

            return result;
        }

        return super.place(context);
    }

    //? if < 1.21 {
    /*@Override
    public Supplier<ItemStackRenderer> getRendererFactory() {
        return CrateItemRenderer::new;
    }
    *///?}
}
