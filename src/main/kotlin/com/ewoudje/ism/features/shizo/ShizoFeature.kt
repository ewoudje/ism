package com.ewoudje.ism.features.shizo

import com.ewoudje.ism.IsmAttributes
import com.ewoudje.ism.IsmAttributes.getVal
import com.ewoudje.ism.IsmPOIs
import com.ewoudje.ism.features.shizo.client.ShizoChestFeature
import com.ewoudje.ism.networking.FakeChestPacket
import com.ewoudje.ism.resource
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.ai.village.poi.PoiManager
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.tick.ServerTickEvent
import net.neoforged.neoforge.network.PacketDistributor

object ShizoFeature {
    private const val chestChancePerTick =  1.0 / 600.0
    private const val creeperChancePerTick =  1.0 / 6000.0
    private val RANDOM_SEQUENCE = "shizo".resource

    @SubscribeEvent
    private fun serverTick(event: ServerTickEvent.Post) {
        event.server.playerList.players.forEach { player -> tick(player.level() as ServerLevel, player) }
    }

    private fun tick(level: ServerLevel, player: ServerPlayer) {
        val random = level.getRandomSequence(RANDOM_SEQUENCE)
        val sanity = IsmAttributes.SANITY.getVal(player);

        if (sanity < 10) {
            val chance = sanity * 0.1 * chestChancePerTick
            if (chance > random.nextDouble()) tripChest(level, player)
        }

        if (sanity < 5) {
            val chance = sanity * 0.2 * creeperChancePerTick
            if (chance > random.nextDouble()) tripCreeper(player)
        }
    }

    fun tripChest(level: ServerLevel, player: ServerPlayer) {
        val poi = level.poiManager.findClosest(
            { it.value() == IsmPOIs.chest },
            player.onPos,
            32,
            PoiManager.Occupancy.ANY
        ).orElse(null)

        poi?.let { PacketDistributor.sendToPlayer(player, FakeChestPacket(it)) }
    }

    fun tripCreeper(player: ServerPlayer) {
        player.playNotifySound(
            SoundEvents.CREEPER_PRIMED,
            SoundSource.HOSTILE,
            1f,
            1f
        )
    }


    fun register(modBus: IEventBus) {
        NeoForge.EVENT_BUS.register(this)
        NeoForge.EVENT_BUS.register(ShizoChestFeature)
    }
}