package dev.chililisoup.condiments.neoforge.compat.create;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import dev.chililisoup.condiments.block.entity.CrateBlockEntity;
import dev.chililisoup.condiments.block.entity.CrateContents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CrateMountedStorageType<T extends CrateMountedStorage> extends MountedItemStorageType<CrateMountedStorage> {
    protected CrateMountedStorageType(MapCodec<T> codec) {
        super(codec);
    }

    @Override
    @Nullable
    public CrateMountedStorage mount(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity be) {
        return Optional.ofNullable(be)
                .map(this::getContents)
                .map(this::createStorage)
                .orElse(null);
    }

    protected CrateContents getContents(BlockEntity blockEntity) {
        return blockEntity instanceof CrateBlockEntity crateBlockEntity ?
                crateBlockEntity.getContents() :
                null;
    }

    protected CrateMountedStorage createStorage(CrateContents contents) {
        return new CrateMountedStorage(this, contents);
    }

    public static final class Impl extends CrateMountedStorageType<CrateMountedStorage> {
        public Impl() {
            super(CrateMountedStorage.CODEC);
        }
    }
}
