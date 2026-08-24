package net.nicetpmod.loot;

import net.nicetpmod.item.ModItems;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

/**
 * Adds the tablet to vanilla loot tables by modifying them in place
 * ({@link LootTableEvents#MODIFY}) instead of shipping a full replacement
 * data pack file, so the rest of the vanilla loot (and any other mod's
 * additions) is left untouched.
 */
public class ModLootTables {

    public static void register() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            // isBuiltin() excludes tables already overridden by a data pack,
            // so this doesn't fight with a resource/data pack replacing the same table.
            if (source.isBuiltin() && BuiltInLootTables.END_CITY_TREASURE.equals(key)) {
                tableBuilder.withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .when(LootItemRandomChanceCondition.randomChance(0.2f))
                        .add(LootItem.lootTableItem(ModItems.TELEPORTATION_TABLET)));
            }
        });
    }
}
