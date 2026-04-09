package dev.chililisoup.condiments.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.chililisoup.condiments.block.entity.CrateContents;
import dev.chililisoup.condiments.item.CrateItem;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Inventory.class)
public abstract class InventoryMixin {
    @WrapOperation(
            method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Inventory;addResource(Lnet/minecraft/world/item/ItemStack;)I"
    ))
    private int addToCrates(Inventory inventory, ItemStack stack, Operation<Integer> original) {
        for (int i = 0; i < inventory.items.size(); i++) {
            ItemStack crateItem = inventory.getItem(i);
            if (!(crateItem.getItem() instanceof CrateItem)) continue;

            CrateContents crateContents = CrateContents.fromCrateItem(crateItem);
            if (!crateContents.autoPickup() || crateContents.itemRecord() == null) continue;

            CrateContents.Mutable mutable = crateContents.toMutable();
            if (mutable.addFromStack(stack) == 0) continue;

            mutable.toImmutable().updateCrateItem(crateItem);
            if (stack.isEmpty()) return stack.getCount();
        }

        return original.call(inventory, stack);
    }
}
