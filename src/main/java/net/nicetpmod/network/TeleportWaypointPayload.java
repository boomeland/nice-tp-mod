package net.nicetpmod.network;

import net.nicetpmod.NiceTPMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** C2S: teleport to a waypoint by index. */
public record TeleportWaypointPayload(int index) implements CustomPacketPayload {
    public static final Type<TeleportWaypointPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(NiceTPMod.MOD_ID, "teleport_waypoint"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TeleportWaypointPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, TeleportWaypointPayload::index,
            TeleportWaypointPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
