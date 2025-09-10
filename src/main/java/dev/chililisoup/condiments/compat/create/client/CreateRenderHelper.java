//? if fabric && < 1.21 {
/*package dev.chililisoup.condiments.compat.create.client;

import com.jozufozu.flywheel.core.virtual.VirtualRenderWorld;
import net.minecraft.world.level.Level;

//$ client_only
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class CreateRenderHelper {
    public static boolean isLevelVirtual(Level level) {
        return level instanceof VirtualRenderWorld;
    }
}
*///?}