package net.nicetpmod.network;

import net.nicetpmod.teleport.Waypoint;
import net.nicetpmod.teleport.WaypointState;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Handles the tablet's C2S payloads. Every handler trusts nothing from the
 * client except intent (add/remove/teleport which index): the player's
 * position and dimension are always read server-side, never taken from
 * the payload, so a modified client can't fake a location or dimension.
 */
public class ServerNetworking {

    private static final long TELEPORT_COOLDOWN_MILLIS = 60_000L;
    // In-memory only: a cooldown doesn't need to survive a server restart.
    private static final Map<UUID, Long> lastTeleportMillis = new HashMap<>();

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(AddWaypointPayload.TYPE, (payload, context) -> {
            // Payload handlers run on the network thread; hop to the server
            // thread before touching player/world/persistent state.
            context.server().execute(() -> {
                ServerPlayer player = context.player();
                String rawName = payload.name();
                String name = rawName.isBlank()
                        ? "Point " + (int) player.getX() + "," + (int) player.getY() + "," + (int) player.getZ()
                        : rawName;
                WaypointState state = WaypointState.getServerState(context.server());
                Waypoint waypoint = new Waypoint(name, player.getX(), player.getY(), player.getZ(), player.level().dimension().identifier());
                boolean added = state.addWaypoint(player.getUUID(), waypoint);
                if (!added) {
                    player.sendSystemMessage(Component.literal("Liste d'emplacements pleine (max " + WaypointState.MAX_WAYPOINTS + ")."), true);
                }
                sendSync(player);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(RemoveWaypointPayload.TYPE, (payload, context) -> {
            context.server().execute(() -> {
                ServerPlayer player = context.player();
                WaypointState.getServerState(context.server()).removeWaypoint(player.getUUID(), payload.index());
                sendSync(player);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(TeleportWaypointPayload.TYPE, (payload, context) ->
                context.server().execute(() -> teleport(context.server(), context.player(), payload.index())));
    }

    private static void teleport(MinecraftServer server, ServerPlayer player, int index) {
        WaypointState state = WaypointState.getServerState(server);
        Waypoint waypoint = state.getWaypoint(player.getUUID(), index);
        if (waypoint == null) {
            return;
        }

        long now = System.currentTimeMillis();
        if (!player.isCreative()) {
            long remainingMillis = TELEPORT_COOLDOWN_MILLIS - (now - lastTeleportMillis.getOrDefault(player.getUUID(), 0L));
            if (remainingMillis > 0) {
                long remainingSeconds = (remainingMillis + 999) / 1000;
                player.sendSystemMessage(Component.literal("Vous devez attendre encore " + remainingSeconds + "s avant de pouvoir vous téléporter à nouveau."), true);
                return;
            }
        }

        Identifier currentDimension = player.level().dimension().identifier();
        if (!currentDimension.equals(waypoint.dimension())) {
            player.sendSystemMessage(Component.literal("Vous devez être dans la même dimension que ce point pour vous y téléporter."), true);
            return;
        }

        if (!player.isCreative()) {
            if (player.experienceLevel < 1) {
                player.sendSystemMessage(Component.literal("Il vous faut au moins 1 niveau d'expérience pour vous téléporter."), true);
                return;
            }
            player.giveExperienceLevels(-1);
        }

        // Same-dimension move only (checked above), so the connection's
        // teleport is enough; no need for the cross-world ServerPlayer#teleportTo.
        player.connection.teleport(waypoint.x(), waypoint.y(), waypoint.z(), player.getYRot(), player.getXRot());
        lastTeleportMillis.put(player.getUUID(), now);
        // false = chat, not the action bar used for the messages above.
        player.sendSystemMessage(Component.literal("Téléportation terminée -> \"" + waypoint.name() + "\""), false);
    }

    public static void sendSync(ServerPlayer player) {
        List<Waypoint> waypoints = WaypointState.getServerState(player.level().getServer()).getWaypoints(player.getUUID());
        ServerPlayNetworking.send(player, new SyncWaypointsPayload(waypoints));
    }
}
