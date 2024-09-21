package dev.chililisoup.condiments.reg;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.chililisoup.condiments.block.AnalogRailBlock;
import dev.chililisoup.condiments.block.RedstoneLedBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedStoneWireBlock;

import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ModColorProviders {
    public static void init() {
        addColorProvider(
                (state, view, pos, tintIndex) ->
                        RedStoneWireBlock.getColorForPower(state.getValue(((AnalogRailBlock) state.getBlock()).getPowerProperty())),
                ModBlocks.ANALOG_RAIL);

        addColorProvider(
                (state, view, pos, tintIndex) ->
                        RedstoneLedBlock.getColorForPower(state.getValue(((RedstoneLedBlock) state.getBlock()).getPowerProperty())),
                ModBlocks.REDSTONE_LED);
    }

    @ExpectPlatform
    public static void addColorProvider(BlockColor color, Supplier<Block> block) {
        throw new AssertionError();
    }
}
