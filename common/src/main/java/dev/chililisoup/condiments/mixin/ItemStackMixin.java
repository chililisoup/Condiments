package dev.chililisoup.condiments.mixin;

import dev.chililisoup.condiments.item.CrateItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemStack.class, priority = 989) // Get in front of Tweakeroo (990)
public abstract class ItemStackMixin {
    @Inject(method = "getMaxStackSize", at = @At("HEAD"), cancellable = true)
    public void getMaxStackSizeInject(CallbackInfoReturnable<Integer> cir) {
        ItemStack item = ((ItemStack) ((Object) this));
        if (item.getItem() instanceof CrateItem && item.getTagElement("BlockEntityTag") != null)
            cir.setReturnValue(1);
    }
}
