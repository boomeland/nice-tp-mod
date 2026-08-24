package net.nicetpmod.network;

import net.nicetpmod.NiceTPMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** C2S: add a waypoint at the sender's current position. */
public record AddWaypointPayload(String name) implements CustomPacketPayload {
    public static final Type<AddWaypointPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(NiceTPMod.MOD_ID, "add_waypoint"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AddWaypointPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, AddWaypointPayload::name,
            AddWaypointPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
