package net.boomeland.nicetpmod.network;

import net.boomeland.nicetpmod.teleport.Waypoint;
import net.boomeland.nicetpmod.teleport.WaypointState;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Handles the tablet's C2S packets. Every handler trusts nothing from the
 * client except intent (add/remove/teleport which index): the player's
 * position and dimension are always read server-side, never taken from
 * the packet, so a modified client can't fake a location or dimension.
 */
public class ServerNetworking {

    private static final long TELEPORT_COOLDOWN_MILLIS = 60_000L;
    // In-memory only: a cooldown doesn't need to survive a server restart.
    private static final Map<UUID, Long> lastTeleportMillis = new HashMap<>();

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(ModNetworking.ADD_WAYPOINT, (server, player, handler, buf, sender) -> {
            String rawName = buf.readString(32);
            // Packet handlers run on the network thread; hop to the server
            // thread before touching player/world/persistent state.
            server.execute(() -> {
                String name = rawName.isBlank()
                        ? "Point " + (int) player.getX() + "," + (int) player.getY() + "," + (int) player.getZ()
                        : rawName;
                WaypointState state = WaypointState.getServerState(server);
                Waypoint waypoint = new Waypoint(name, player.getX(), player.getY(), player.getZ(), player.getWorld().getRegistryKey().getValue());
                boolean added = state.addWaypoint(player.getUuid(), waypoint);
                if (!added) {
                    player.sendMessage(Text.literal("Liste d'emplacements pleine (max " + WaypointState.MAX_WAYPOINTS + ")."), true);
                }
                sendSync(player);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(ModNetworking.REMOVE_WAYPOINT, (server, player, handler, buf, sender) -> {
            int index = buf.readInt();
            server.execute(() -> {
                WaypointState.getServerState(server).removeWaypoint(player.getUuid(), index);
                sendSync(player);
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(ModNetworking.TELEPORT_WAYPOINT, (server, player, handler, buf, sender) -> {
            int index = buf.readInt();
            server.execute(() -> teleport(server, player, index));
        });
    }

    private static void teleport(MinecraftServer server, ServerPlayerEntity player, int index) {
        WaypointState state = WaypointState.getServerState(server);
        Waypoint waypoint = state.getWaypoint(player.getUuid(), index);
        if (waypoint == null) {
            return;
        }

        long now = System.currentTimeMillis();
        if (!player.getAbilities().creativeMode) {
            long remainingMillis = TELEPORT_COOLDOWN_MILLIS - (now - lastTeleportMillis.getOrDefault(player.getUuid(), 0L));
            if (remainingMillis > 0) {
                long remainingSeconds = (remainingMillis + 999) / 1000;
                player.sendMessage(Text.literal("Vous devez attendre encore " + remainingSeconds + "s avant de pouvoir vous téléporter à nouveau."), true);
                return;
            }
        }

        Identifier currentDimension = player.getWorld().getRegistryKey().getValue();
        if (!currentDimension.equals(waypoint.dimension())) {
            player.sendMessage(Text.literal("Vous devez être dans la même dimension que ce point pour vous y téléporter."), true);
            return;
        }

        if (!player.getAbilities().creativeMode) {
            if (player.experienceLevel < 1) {
                player.sendMessage(Text.literal("Il vous faut au moins 1 niveau d'expérience pour vous téléporter."), true);
                return;
            }
            player.addExperienceLevels(-1);
        }

        // Same-dimension move only (checked above), so the network handler's
        // teleport is enough; no need for the cross-world ServerPlayerEntity#teleport.
        player.networkHandler.requestTeleport(waypoint.x(), waypoint.y(), waypoint.z(), player.getYaw(), player.getPitch());
        lastTeleportMillis.put(player.getUuid(), now);
        // false = chat, not the action bar used for the messages above.
        player.sendMessage(Text.literal("Téléportation terminée -> \"" + waypoint.name() + "\""), false);
    }

    public static void sendSync(ServerPlayerEntity player) {
        List<Waypoint> waypoints = WaypointState.getServerState(player.getServer()).getWaypoints(player.getUuid());
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(waypoints.size());
        for (Waypoint waypoint : waypoints) {
            buf.writeString(waypoint.name());
            buf.writeDouble(waypoint.x());
            buf.writeDouble(waypoint.y());
            buf.writeDouble(waypoint.z());
            buf.writeString(waypoint.dimension().toString());
        }
        ServerPlayNetworking.send(player, ModNetworking.SYNC_WAYPOINTS, buf);
    }
}
