package net.nicetpmod.teleport;

import com.mojang.serialization.Codec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Waypoints belong to the player (UUID), not to a specific tablet item
 * stack, so any tablet the player holds sees the same list and it
 * survives the item being lost, dropped or duplicated. Stored as a
 * single {@link SavedData} on the overworld save rather than in
 * per-player data, since that's the simplest place both client-triggered
 * add/remove/teleport packets can reach without extra plumbing.
 */
public class WaypointState extends SavedData {
    public static final int MAX_WAYPOINTS = 12;

    private static final Codec<Map<UUID, List<Waypoint>>> WAYPOINTS_CODEC =
            Codec.unboundedMap(UUIDUtil.STRING_CODEC, Waypoint.CODEC.listOf());

    public static final SavedDataType<WaypointState> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath("nicetpmod", "waypoints"),
            WaypointState::new,
            WAYPOINTS_CODEC.xmap(WaypointState::new, state -> state.waypointsByPlayer),
            DataFixTypes.LEVEL
    );

    private final Map<UUID, List<Waypoint>> waypointsByPlayer;

    public WaypointState() {
        this(new HashMap<>());
    }

    private WaypointState(Map<UUID, List<Waypoint>> waypointsByPlayer) {
        this.waypointsByPlayer = waypointsByPlayer;
    }

    public static WaypointState getServerState(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
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
        setDirty();
        return true;
    }

    public boolean removeWaypoint(UUID playerId, int index) {
        List<Waypoint> list = waypointsByPlayer.get(playerId);
        if (list == null || index < 0 || index >= list.size()) {
            return false;
        }
        list.remove(index);
        setDirty();
        return true;
    }
}
