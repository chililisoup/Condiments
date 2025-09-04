//? if forgeLike {
/*package dev.chililisoup.condiments.compat.create;

import com.simibubi.create.api.contraption.storage.item.MountedItemStorageType;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.chililisoup.condiments.Condiments;
import dev.chililisoup.condiments.reg.ModBlockTags;
import net.neoforged.bus.api.IEventBus;

public class CreateCompat {
    private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(Condiments.MOD_ID);

    public static final RegistryEntry<MountedItemStorageType<?>, CrateMountedStorageType.Impl> CRATE_MOUNTED_STORAGE =
            REGISTRATE.mountedItemStorage("crate", CrateMountedStorageType.Impl::new)
                    .associateBlockTag(ModBlockTags.CRATES)
                    .register();

    public static void init(IEventBus modEventBus) {
        REGISTRATE.registerEventListeners(modEventBus);
    }
}
*///?}