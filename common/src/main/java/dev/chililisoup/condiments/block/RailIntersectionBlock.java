package dev.chililisoup.condiments.block;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class RailIntersectionBlock extends BaseRailBlock implements CondimentsRail {
    public static final MapCodec<RailIntersectionBlock> CODEC = simpleCodec(RailIntersectionBlock::new);
    public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;

    public RailIntersectionBlock(Properties properties) {
        super(true, properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseRailBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SHAPE, WATERLOGGED);
    }

    @Override
    public Pair<Vec3i, Vec3i> getExits(RailShape shape, BlockState state, Vec3 deltaMovement, Operation<Pair<Vec3i, Vec3i>> original) {
        if (Math.abs(deltaMovement.z) > Math.abs((deltaMovement.x)))
            return original.call(RailShape.NORTH_SOUTH);
        return original.call(RailShape.EAST_WEST);
    }
}
