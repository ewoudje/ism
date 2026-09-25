package com.ewoudje.ism.util.world;

import com.ewoudje.ism.collections.IsmPackets;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.biome.Biome;

public record SingleBiomeUpdatePacket(long packedQuart, Holder<Biome> biome) implements CustomPacketPayload {
    public static final Type<SingleBiomeUpdatePacket> TYPE = IsmPackets.type("single_biome_update");
    public static final StreamCodec<RegistryFriendlyByteBuf, SingleBiomeUpdatePacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.LONG, SingleBiomeUpdatePacket::packedQuart,
                    ByteBufCodecs.holderRegistry(Registries.BIOME), SingleBiomeUpdatePacket::biome,
                    SingleBiomeUpdatePacket::new
            );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
