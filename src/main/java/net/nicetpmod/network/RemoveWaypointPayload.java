package net.nicetpmod.network;

import net.nicetpmod.NiceTPMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** C2S: delete a waypoint by index. */
public record RemoveWaypointPayload(int index) implements CustomPacketPayload {
    public static final Type<RemoveWaypointPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(NiceTPMod.MOD_ID, "remove_waypoint"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RemoveWaypointPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, RemoveWaypointPayload::index,
            RemoveWaypointPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
