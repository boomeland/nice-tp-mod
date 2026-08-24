package net.nicetpmod.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

/**
 * Registers the tablet's payload types. The server is the source of truth
 * for waypoints: the three C2S payloads only ever request a change, and the
 * server always answers with a full {@link SyncWaypointsPayload} snapshot,
 * which the client uses to open or refresh the GUI.
 */
public class ModNetworking {
    public static void registerPayloadTypes() {
        PayloadTypeRegistry.serverboundPlay().register(AddWaypointPayload.TYPE, AddWaypointPayload.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(RemoveWaypointPayload.TYPE, RemoveWaypointPayload.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(TeleportWaypointPayload.TYPE, TeleportWaypointPayload.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncWaypointsPayload.TYPE, SyncWaypointsPayload.STREAM_CODEC);
    }
}
