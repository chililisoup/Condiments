package dev.chililisoup.condiments.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class RedstoneLedBlock extends Block {
    public static final IntegerProperty POWER;
    private static final Vec3[] COLORS;

    public RedstoneLedBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(POWER, 0));
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (level.isClientSide) return;

        int power = level.getBestNeighborSignal(pos);
        if (power == state.getValue(POWER)) return;

        level.setBlock(pos, state.setValue(POWER, power), 3);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(POWER, context.getLevel().getBestNeighborSignal(context.getClickedPos()));
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return state.getValue(POWER);
    }

    public Property<Integer> getPowerProperty() {
        return POWER;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWER);
    }

    public static int getColorForPower(int power) {
        Vec3 vec3 = COLORS[power];
        return Mth.color((float)vec3.x(), (float)vec3.y(), (float)vec3.z());
    }

    static {
        POWER = BlockStateProperties.POWER;
        COLORS = new Vec3[]{
                new Vec3(1.0 / 8,  1.0 / 8,  1.0 / 8), // black
                new Vec3(0,         0,         2.0 / 3 ), // dark_blue
                new Vec3(0,         2.0 / 3,   0       ), // dark_green
                new Vec3(0,         2.0 / 3,   2.0 / 3 ), // dark_aqua
                new Vec3(2.0 / 3,   0,         0       ), // dark_red
                new Vec3(2.0 / 3,   0,         2.0 / 3 ), // dark_purple
                new Vec3(1,         2.0 / 3,   0       ), // gold
                new Vec3(2.0 / 3,   2.0 / 3,   2.0 / 3 ), // gray
                new Vec3(1.0 / 3,   1.0 / 3,   1.0 / 3 ), // dark_gray
                new Vec3(1.0 / 3,   1.0 / 3,   1       ), // blue
                new Vec3(1.0 / 3,   1,         1.0 / 3 ), // green
                new Vec3(1.0 / 3,   1,         1       ), // aqua
                new Vec3(1,         1.0 / 3,   1.0 / 3 ), // red
                new Vec3(1,         1.0 / 3,   1       ), // light_purple
                new Vec3(1,         1,         1.0 / 3 ), // yellow
                new Vec3(1,         1,         1       )  // white
        };
    }
}
