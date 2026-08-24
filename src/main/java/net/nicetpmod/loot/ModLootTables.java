package net.boomeland.nicetpmod.loot;

import net.boomeland.nicetpmod.item.ModItems;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;

/**
 * Adds the tablet to vanilla loot tables by modifying them in place
 * ({@link LootTableEvents#MODIFY}) instead of shipping a full replacement
 * data pack file, so the rest of the vanilla loot (and any other mod's
 * additions) is left untouched.
 */
public class ModLootTables {

    public static void register() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            // isBuiltin() excludes tables already overridden by a data pack,
            // so this doesn't fight with a resource/data pack replacing the same table.
            if (source.isBuiltin() && LootTables.END_CITY_TREASURE_CHEST.equals(id)) {
                tableBuilder.pool(LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.2f))
                        .with(ItemEntry.builder(ModItems.TELEPORTATION_TABLET)));
            }
        });
    }
}
