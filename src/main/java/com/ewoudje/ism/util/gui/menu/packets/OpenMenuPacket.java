package com.ewoudje.ism.util.gui.menu.packets;

import com.ewoudje.ism.collections.IsmPackets;
import com.ewoudje.ism.util.gui.menu.CustomMenuType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record OpenMenuPacket(CustomMenuType<?> menuType, int id, CompoundTag data) implements CustomPacketPayload {

    public static final Type<OpenMenuPacket> TYPE = IsmPackets.type("open_menu");
    public static StreamCodec<RegistryFriendlyByteBuf, OpenMenuPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(CustomMenuType.KEY), OpenMenuPacket::menuType,
            ByteBufCodecs.VAR_INT, OpenMenuPacket::id,
            ByteBufCodecs.COMPOUND_TAG, OpenMenuPacket::data,
            OpenMenuPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
