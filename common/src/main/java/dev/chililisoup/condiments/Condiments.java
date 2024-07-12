package dev.chililisoup.condiments;

import dev.chililisoup.condiments.reg.ModBlocks;
import dev.chililisoup.condiments.reg.ModColorProviders;
import dev.chililisoup.condiments.reg.ModRecipeSerializers;;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Condiments {
	public static final String MOD_ID = "condiments";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static void init() {
		ModBlocks.init();
		ModRecipeSerializers.init();
	}

	@Environment(EnvType.CLIENT)
	public static void initClient() {
		ModColorProviders.init();
	}
}
