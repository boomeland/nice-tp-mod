package net.boomeland.nicetpmod.teleport;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Waypoints belong to the player (UUID), not to a specific tablet item
 * stack, so any tablet the player holds sees the same list and it
 * survives the item being lost, dropped or duplicated. Stored as a
 * single {@link PersistentState} on the overworld save rather than in
 * per-player data, since that's the simplest place both client-triggered
 * add/remove/teleport packets can reach without extra plumbing.
 */
public class WaypointState extends PersistentState {
    public static final int MAX_WAYPOINTS = 12;
    private static final String ID = "nicetpmod_waypoints";

    private final Map<UUID, List<Waypoint>> waypointsByPlayer = new HashMap<>();

    public static WaypointState getServerState(MinecraftServer server) {
        PersistentStateManager manager = server.getOverworld().getPersistentStateManager();
        return manager.getOrCreate(WaypointState::createFromNbt, WaypointState::new, ID);
    }

    public List<Waypoint> getWaypoints(UUID playerId) {
        return waypointsByPlayer.getOrDefault(playerId, List.of());
    }

    public Waypoint getWaypoint(UUID playerId, int index) {
        List<Waypoint> list = waypointsByPlayer.get(playerId);
        if (list == null || index < 0 || index >= list.size()) {
            return null;
        }
        return list.get(index);
    }

    public boolean addWaypoint(UUID playerId, Waypoint waypoint) {
        List<Waypoint> list = waypointsByPlayer.computeIfAbsent(playerId, id -> new ArrayList<>());
        if (list.size() >= MAX_WAYPOINTS) {
            return false;
        }
        list.add(waypoint);
        markDirty();
        return true;
    }

    public boolean removeWaypoint(UUID playerId, int index) {
        List<Waypoint> list = waypointsByPlayer.get(playerId);
        if (list == null || index < 0 || index >= list.size()) {
            return false;
        }
        list.remove(index);
        markDirty();
        return true;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList players = new NbtList();
        for (Map.Entry<UUID, List<Waypoint>> entry : waypointsByPlayer.entrySet()) {
            NbtCompound playerNbt = new NbtCompound();
            playerNbt.putUuid("Player", entry.getKey());

            NbtList waypoints = new NbtList();
            for (Waypoint waypoint : entry.getValue()) {
                NbtCompound waypointNbt = new NbtCompound();
                waypointNbt.putString("Name", waypoint.name());
                waypointNbt.putDouble("X", waypoint.x());
                waypointNbt.putDouble("Y", waypoint.y());
                waypointNbt.putDouble("Z", waypoint.z());
                waypointNbt.putString("Dimension", waypoint.dimension().toString());
                waypoints.add(waypointNbt);
            }
            playerNbt.put("Waypoints", waypoints);
            players.add(playerNbt);
        }
        nbt.put("Players", players);
        return nbt;
    }

    public static WaypointState createFromNbt(NbtCompound nbt) {
        WaypointState state = new WaypointState();
        NbtList players = nbt.getList("Players", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < players.size(); i++) {
            NbtCompound playerNbt = players.getCompound(i);
            UUID playerId = playerNbt.getUuid("Player");

            List<Waypoint> waypoints = new ArrayList<>();
            NbtList waypointsNbt = playerNbt.getList("Waypoints", NbtElement.COMPOUND_TYPE);
            for (int j = 0; j < waypointsNbt.size(); j++) {
                NbtCompound w = waypointsNbt.getCompound(j);
                waypoints.add(new Waypoint(
                        w.getString("Name"),
                        w.getDouble("X"),
                        w.getDouble("Y"),
                        w.getDouble("Z"),
                        new Identifier(w.getString("Dimension"))
                ));
            }
            state.waypointsByPlayer.put(playerId, waypoints);
        }
        return state;
    }
}
