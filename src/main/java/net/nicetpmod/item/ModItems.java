package net.nicetpmod.item;

import net.nicetpmod.NiceTPMod;
import net.nicetpmod.item.custom.TeleportationTabletItem;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public class ModItems {

    public static final Item TELEPORTATION_TABLET = registerItem("teleportation_tablet",
            new Item.Properties().stacksTo(1), TeleportationTabletItem::new);

    private static Item registerItem(String name, Item.Properties properties, java.util.function.Function<Item.Properties, Item> factory) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(NiceTPMod.MOD_ID, name));
        Item item = factory.apply(properties.setId(key));
        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }

    public static void registerModItems() {
        NiceTPMod.LOGGER.info("Registering mod Items for " + NiceTPMod.MOD_ID);
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register(output -> output.accept(TELEPORTATION_TABLET));
    }
}
