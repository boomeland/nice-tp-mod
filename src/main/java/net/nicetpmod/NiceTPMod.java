package net.nicetpmod;

import net.nicetpmod.item.ModItems;
import net.nicetpmod.loot.ModLootTables;
import net.nicetpmod.network.ServerNetworking;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NiceTPMod implements ModInitializer {
	public static final String MOD_ID = "nicetpmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ModItems.registerModItems();
		ServerNetworking.register();
		ModLootTables.register();
	}
}