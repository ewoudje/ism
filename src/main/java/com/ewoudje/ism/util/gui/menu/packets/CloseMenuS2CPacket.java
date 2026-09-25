package com.ewoudje.ism.util.gui.menu.packets;

import com.ewoudje.ism.collections.IsmPackets;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record CloseMenuS2CPacket(int id) implements CustomPacketPayload {

    public static final Type<CloseMenuS2CPacket> TYPE = IsmPackets.type("close_menu_s2c");
    public static StreamCodec<RegistryFriendlyByteBuf, CloseMenuS2CPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, CloseMenuS2CPacket::id,
            CloseMenuS2CPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
