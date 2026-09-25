package com.ewoudje.ism.util.gui.menu.packets;

import com.ewoudje.ism.collections.IsmPackets;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record MenuEventPacket(ByteBuf data) implements CustomPacketPayload {
    public static final Type<MenuEventPacket> S2C = IsmPackets.type("menu_event_s2c");
    public static final Type<MenuEventPacket> C2S = IsmPackets.type("menu_event_c2s");
    public static final StreamCodec<ByteBuf, MenuEventPacket> STREAM_CODEC = StreamCodec.of(
            (output, value) -> {
                output.writeInt(value.data().readableBytes());
                output.writeBytes(value.data());
            },
            input -> {
                int size = input.readInt();
                return new MenuEventPacket(input.readBytes(size));
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return null;
    }
}
