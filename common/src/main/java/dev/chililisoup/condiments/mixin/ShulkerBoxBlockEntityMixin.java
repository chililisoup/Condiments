package dev.chililisoup.condiments.mixin;

import dev.chililisoup.condiments.item.CrateItem;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerBoxBlockEntity.class)
public abstract class ShulkerBoxBlockEntityMixin {
    @Inject(method = "canPlaceItemThroughFace", at = @At("RETURN"), cancellable = true)
    private void canPlaceInject(int index, ItemStack item, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (item.getItem() instanceof CrateItem) cir.setReturnValue(false);
    }
}
