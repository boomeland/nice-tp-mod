package net.boomeland.nicetpmod.item.custom.teleportationTabletItem;

import net.minecraft.item.Item;
import net.minecraft.entity.player.PlayerEntity;


import java.util.HashMap;
import java.util.Map;

public class TeleportationTabletItem extends Item {
    private static final Map<PlayerEntity, Integer> playerSelectedPosition = new HashMap<>();

    public TeleportationTabletItem(Settings settings) {
        super(settings);
    }

}

