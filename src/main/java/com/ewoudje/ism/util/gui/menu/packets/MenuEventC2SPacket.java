package com.ewoudje.ism.util.gui.menu.packets;

import com.ewoudje.ism.collections.IsmPackets;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record MenuEventC2SPacket(ByteBuf data) implements CustomPacketPayload {
    public static final Type<MenuEventC2SPacket> TYPE = IsmPackets.type("menu_event_c2s");
    public static final StreamCodec<ByteBuf, MenuEventC2SPacket> STREAM_CODEC = StreamCodec.of(
            (output, value) -> {
                ByteBuf buf = value.data();
                buf.setIndex(0, buf.writerIndex());
                output.writeInt(buf.readableBytes());
                output.writeBytes(value.data());
            },
            input -> {
                int size = input.readInt();
                return new MenuEventC2SPacket(input.readBytes(size));
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
