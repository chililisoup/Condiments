package dev.chililisoup.condiments;

import dev.chililisoup.condiments.reg.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Condiments {
	public static final String MOD_ID = "condiments";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static void init() {
		ModBlocks.init();
		ModItems.init();
		ModRecipeSerializers.init();
		ModWaxingPairs.init();
	}

	@Environment(EnvType.CLIENT)
	public static void initClient() {
		ModColorProviders.init();
	}
}
