package dev.chililisoup.condiments;

import dev.chililisoup.condiments.config.ClientConfig;
import dev.chililisoup.condiments.config.CommonConfig;
import dev.chililisoup.condiments.dynamicpack.ClientDynamicResourcesGenerator;
import dev.chililisoup.condiments.reg.ModBlockSetVariants;
import dev.chililisoup.condiments.dynamicpack.ServerDynamicResourcesGenerator;
import dev.chililisoup.condiments.reg.*;
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Condiments {
	public static final String MOD_ID = "condiments";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public static ResourceLocation loc(String id) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, id);
	}

	public static void init() {
		CommonConfig.init();

		ModComponents.init();
		ModBlocks.init();
		ModItems.init();
		ModBlockSetVariants.init();
		ModBlockEntities.init();
		ModCreativeTabs.init();
		ModRecipeSerializers.init();
		ModDispenserBehaviors.init();

		ServerDynamicResourcesGenerator.INSTANCE.register();

		if (PlatHelper.getPhysicalSide().isClient()) {
			ClientConfig.init();
			ClientRegistry.init();
			ClientDynamicResourcesGenerator.INSTANCE.register();
		}

		PlatHelper.addCommonSetup(Condiments::setup);
	}

	private static void setup() {
		ModWaxingPairs.init();
	}
}
