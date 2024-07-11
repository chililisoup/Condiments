package dev.chililisoup.condiments.extra;

import dev.chililisoup.condiments.Condiments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import org.jetbrains.annotations.NotNull;

public class CrateDispenserBehavior extends OptionalDispenseItemBehavior {
    public CrateDispenserBehavior() {
    }

    @Override
    protected @NotNull ItemStack execute(BlockSource source, ItemStack stack) {
        this.setSuccess(false);
        Item item = stack.getItem();
        if (item instanceof BlockItem) {
            Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
            BlockPos blockPos = source.getPos().relative(direction);

            try {
                this.setSuccess(((BlockItem)item).place(new DirectedPlaceContext(source.getLevel(), blockPos, direction, stack)).consumesAction());
            } catch (Exception exception) {
                Condiments.LOGGER.error("Error trying to place crate at {}", blockPos, exception);
            }
        }

        return stack;
    }
}
