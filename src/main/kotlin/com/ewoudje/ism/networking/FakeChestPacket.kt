package com.ewoudje.ism.networking

import com.ewoudje.ism.client.ClientFogHandler
import com.ewoudje.ism.features.fog.FogState
import com.ewoudje.ism.features.shizo.client.ShizoChestFeature
import com.ewoudje.ism.resource
import net.minecraft.core.BlockPos
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext

class FakeChestPacket(val blockPos: BlockPos) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<FogUpdatePacket> = TYPE

    fun handle(ctx: IPayloadContext) {
        ShizoChestFeature.trigger(blockPos);
    }


    companion object {
        val TYPE = CustomPacketPayload.Type<FogUpdatePacket>("fog_update".resource)
        val STREAM_CODEC = FogState.STREAM_CODEC.map(::FogUpdatePacket, FogUpdatePacket::fogState)
    }
}