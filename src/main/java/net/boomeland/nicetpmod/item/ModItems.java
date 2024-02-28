package net.boomeland.nicetpmod.item;

import net.boomeland.nicetpmod.NiceTPMod;
import net.boomeland.nicetpmod.item.custom.teleportationTabletItem.TeleportationTabletItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item TELEPORTATION_TABLET = registerItem("teleportation_tablet", new TeleportationTabletItem(new FabricItemSettings().maxCount(1)));

    private static void addItemsToIngredientItemGroup(FabricItemGroupEntries entries){
        entries.add(TELEPORTATION_TABLET);
    }
    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(NiceTPMod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        NiceTPMod.LOGGER.info("Registering mod Items for " + NiceTPMod.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(ModItems::addItemsToIngredientItemGroup);
    }
}
