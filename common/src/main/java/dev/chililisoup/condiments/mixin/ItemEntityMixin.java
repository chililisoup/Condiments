package dev.chililisoup.condiments.mixin;

import dev.chililisoup.condiments.item.CrateItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/item/ItemStack;)V", at = @At("RETURN"))
    private void itemEntityInitInject(Level level, double posX, double posY, double posZ, ItemStack stack, CallbackInfo ci) {
        if (stack.getItem() instanceof CrateItem) {
            CompoundTag compoundTag = stack.getTagElement("BlockEntityTag");
            if (compoundTag == null) return;

            if (compoundTag.getBoolean("CrateLocked")) return;

            if (compoundTag.getCompound("CrateItems").getShort("Count") <= 0)
                stack.removeTagKey("BlockEntityTag");
        }
    }
}
