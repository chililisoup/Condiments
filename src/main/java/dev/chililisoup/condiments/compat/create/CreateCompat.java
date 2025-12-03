//? if forge_like || < 1.21 {
/*package dev.chililisoup.condiments.compat.create;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.reg.ModBlockTags;

//? if >= 1.21
import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;

//? if neoforge {
/^import net.neoforged.bus.api.IEventBus;
^///?} else if forge {
/^import net.minecraftforge.eventbus.api.IEventBus;
^///?} else {
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import dev.chililisoup.condiments.item.CrateItemHandler;
import dev.chililisoup.condiments.reg.ModBlockEntities;
//?}

public class CreateCompat {
    private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(Condiments.MOD_ID);

    //? if < 1.21 {
    /^public static final RegistryEntry<CrateMountedStorageType.Impl> CRATE_MOUNTED_STORAGE =
    ^///?} else
    public static final RegistryEntry<MountedItemStorageType<?>, CrateMountedStorageType.Impl> CRATE_MOUNTED_STORAGE =
            REGISTRATE.mountedItemStorage("crate", CrateMountedStorageType.Impl::new)
                    .associateBlockTag(ModBlockTags.CRATES)
                    .register();

    //? if forge_like {
    /^public static void init(IEventBus modEventBus) {
        REGISTRATE.registerEventListeners(modEventBus);
    }^///?} else {
    public static void init() {
        REGISTRATE.register();

        ItemStorage.SIDED.registerForBlockEntity(
                (blockEntity, direction) -> new CrateItemHandler(blockEntity),
                ModBlockEntities.CRATE_BE_TYPE.get()
        );
    }
    //?}
}
*///?}