//? if forge_like || < 1.21 {
/*package dev.chililisoup.condiments.compat.create;

import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import dev.chililisoup.condiments.block.entity.CrateBlockEntity;
import dev.chililisoup.condiments.item.CrateItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

//? if < 1.21 {
/^import com.mojang.serialization.Codec;
^///?} else
import com.mojang.serialization.MapCodec;

import java.util.Optional;

public class CrateMountedStorageType<T extends CrateMountedStorage> extends MountedItemStorageType<CrateMountedStorage> {
    protected CrateMountedStorageType(/^? if < 1.21 {^/ /^Codec ^//^?} else {^/ MapCodec /^?}^/<T> codec) {
        super(codec);
    }

    @Override
    @Nullable
    public CrateMountedStorage mount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
        return Optional.ofNullable(be)
                .map(this::getCrate)
                .map(this::createStorage)
                .orElse(null);
    }

    protected CrateBlockEntity getCrate(BlockEntity blockEntity) {
        return blockEntity instanceof CrateBlockEntity crateBlockEntity ?
                crateBlockEntity :
                null;
    }

    protected CrateMountedStorage createStorage(CrateBlockEntity crate) {
        return new CrateMountedStorage(this,
                //? if forge_like {
                /^crate.getContents()
                ^///?} else
                new CrateItemHandler(crate)
        );
    }

    public static final class Impl extends CrateMountedStorageType<CrateMountedStorage> {
        public Impl() {
            super(CrateMountedStorage.CODEC);
        }
    }
}
*///?}