package com.ewoudje.ism.features.shizo.client

import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.player.LocalPlayer
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.level.block.Blocks
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.client.event.ClientTickEvent

object ShizoChestFeature {
    private var currentChestTripped: BlockPos? = null
    private var chestTrippedTimer: Int = 0

    private fun tick(level: ClientLevel, player: LocalPlayer) {
        if (currentChestTripped != null) {
            chestTrippedTimer--
            if (chestTrippedTimer <= 0) {
                level.blockEvent(currentChestTripped, Blocks.CHEST, 1, 0)
                currentChestTripped = null
            }
        }
    }

    fun trigger(blockPos: BlockPos) {
        val level = Minecraft.getInstance().level ?: return
        currentChestTripped = blockPos;

        level.blockEvent(blockPos, Blocks.CHEST, 1, 1)
        level.playLocalSound(
            blockPos,
            SoundEvents.CHEST_OPEN,
            SoundSource.BLOCKS,
            0.5F, 0.95F,
            true
        )

        chestTrippedTimer = 0
    }

    @SubscribeEvent
    private fun tick(event: ClientTickEvent.Post) {
        val instance = Minecraft.getInstance()
        val player = instance.player ?: return
        val level = instance.level ?: return

        tick(level, player)
    }
}