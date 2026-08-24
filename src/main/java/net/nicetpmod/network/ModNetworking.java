package net.nicetpmod.network;

import net.nicetpmod.NiceTPMod;
import net.minecraft.util.Identifier;

/**
 * Channel ids for the tablet GUI. The server is the source of truth for
 * waypoints: the three C2S channels only ever request a change, and the
 * server always answers with a full {@link #SYNC_WAYPOINTS} snapshot,
 * which the client uses to open or refresh the GUI.
 */
public class ModNetworking {
    /** C2S: add a waypoint at the sender's current position. Payload: name (string). */
    public static final Identifier ADD_WAYPOINT = new Identifier(NiceTPMod.MOD_ID, "add_waypoint");
    /** C2S: delete a waypoint. Payload: index (int). */
    public static final Identifier REMOVE_WAYPOINT = new Identifier(NiceTPMod.MOD_ID, "remove_waypoint");
    /** C2S: teleport to a waypoint. Payload: index (int). */
    public static final Identifier TELEPORT_WAYPOINT = new Identifier(NiceTPMod.MOD_ID, "teleport_waypoint");
    /** S2C: full waypoint list snapshot, sent after every change and on tablet use. */
    public static final Identifier SYNC_WAYPOINTS = new Identifier(NiceTPMod.MOD_ID, "sync_waypoints");
}
