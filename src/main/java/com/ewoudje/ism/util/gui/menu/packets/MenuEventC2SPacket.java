package com.ewoudje.ism.util.gui.menu.packets;

import com.ewoudje.ism.collections.IsmPackets;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record MenuEventS2CPacket(ByteBuf data) implements CustomPacketPayload {
    public static final Type<MenuEventS2CPacket> TYPE = IsmPackets.type("menu_event_s2c");
    public static final StreamCodec<ByteBuf, MenuEventS2CPacket> STREAM_CODEC = StreamCodec.of(
            (output, value) -> {
                output.writeInt(value.data().readableBytes());
                output.writeBytes(value.data());
            },
            input -> {
                int size = input.readInt();
                return new MenuEventS2CPacket(input.readBytes(size));
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
