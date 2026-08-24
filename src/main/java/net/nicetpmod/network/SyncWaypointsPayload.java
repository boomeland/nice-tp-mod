package net.nicetpmod.network;

import net.nicetpmod.NiceTPMod;
import net.nicetpmod.teleport.Waypoint;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

/** S2C: full waypoint list snapshot, sent after every change and on tablet use. */
public record SyncWaypointsPayload(List<Waypoint> waypoints) implements CustomPacketPayload {
    public static final Type<SyncWaypointsPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(NiceTPMod.MOD_ID, "sync_waypoints"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncWaypointsPayload> STREAM_CODEC = CustomPacketPayload.codec(
            (payload, buf) -> {
                buf.writeInt(payload.waypoints().size());
                for (Waypoint waypoint : payload.waypoints()) {
                    buf.writeUtf(waypoint.name());
                    buf.writeDouble(waypoint.x());
                    buf.writeDouble(waypoint.y());
                    buf.writeDouble(waypoint.z());
                    buf.writeUtf(waypoint.dimension().toString());
                }
            },
            buf -> {
                int count = buf.readInt();
                List<Waypoint> waypoints = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    String name = buf.readUtf(32);
                    double x = buf.readDouble();
                    double y = buf.readDouble();
                    double z = buf.readDouble();
                    Identifier dimension = Identifier.parse(buf.readUtf());
                    waypoints.add(new Waypoint(name, x, y, z, dimension));
                }
                return new SyncWaypointsPayload(waypoints);
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
