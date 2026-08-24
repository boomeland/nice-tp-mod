package net.boomeland.nicetpmod.network;

import net.boomeland.nicetpmod.NiceTPMod;
import net.minecraft.util.Identifier;

public class ModNetworking {
    public static final Identifier ADD_WAYPOINT = new Identifier(NiceTPMod.MOD_ID, "add_waypoint");
    public static final Identifier REMOVE_WAYPOINT = new Identifier(NiceTPMod.MOD_ID, "remove_waypoint");
    public static final Identifier TELEPORT_WAYPOINT = new Identifier(NiceTPMod.MOD_ID, "teleport_waypoint");
    public static final Identifier SYNC_WAYPOINTS = new Identifier(NiceTPMod.MOD_ID, "sync_waypoints");
}
