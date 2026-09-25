package com.ewoudje.ism.util.gui.menu.packets;

import com.ewoudje.ism.collections.IsmPackets;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Only S2C
 */
public record CloseMenuPacket(int id) implements CustomPacketPayload {

    public static final Type<CloseMenuPacket> TYPE = IsmPackets.type("close_menu");
    public static StreamCodec<RegistryFriendlyByteBuf, CloseMenuPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, CloseMenuPacket::id,
            CloseMenuPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
