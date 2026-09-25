package com.ewoudje.ism.util.gui.menu.packets;

import com.ewoudje.ism.collections.IsmPackets;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record CloseMenuC2SPacket(int id) implements CustomPacketPayload {

    public static final Type<CloseMenuC2SPacket> TYPE = IsmPackets.type("close_menu_c2s");
    public static StreamCodec<RegistryFriendlyByteBuf, CloseMenuC2SPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, CloseMenuC2SPacket::id,
            CloseMenuC2SPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
