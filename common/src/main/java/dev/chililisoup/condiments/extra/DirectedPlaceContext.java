package dev.chililisoup.condiments.extra;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class DirectedPlaceContext extends DirectionalPlaceContext {
    private final Direction lookDirection;

    public DirectedPlaceContext(Level level, BlockPos blockPos, Direction direction, ItemStack itemStack) {
        super(level, blockPos, direction, itemStack, direction);
        this.lookDirection = direction.getOpposite();
    }

    @Override
    public @NotNull Direction getNearestLookingDirection() {
        return lookDirection;
    }
}
