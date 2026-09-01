package com.ewoudje.ism.client

import com.ewoudje.ism.client.particles.IsmParticles
import com.ewoudje.ism.client.renderers.RollingFogRenderer
import com.ewoudje.ism.features.fog.FogState
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import org.joml.Vector3d
import org.joml.Vector3f
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVector3d

object ClientFogHandler {
    private val FOG_DISTANCE = 512.0
    private var lastFog: FogState? = null
    private var whisperChance = 0.0
    private var visionChance = 0.0

    fun tick() {
        if (Minecraft.getInstance().isPaused) return
        val player = Minecraft.getInstance().player ?: return

        if (lastFog?.isInFog(player.position().toVector3d()) == true) {
            if (whisperChance > 0.005 && player.random.nextDouble() < whisperChance) {
                Minecraft.getInstance().soundManager.play(SimpleSoundInstance.forAmbientAddition(IsmSounds.WHISPERS))
                whisperChance = 0.0
            } else {
                whisperChance += 0.0001
            }

            if (visionChance > 0.01 && player.random.nextDouble() < visionChance) {
                val direction = Vector3d(player.random.nextDouble() - 0.5, 0.0, player.random.nextDouble() - 0.5)
                    .normalize(player.random.nextDouble() * 5 + 2)

                val particlePos = player.eyePosition.toVector3d().add(direction)
                Minecraft.getInstance().particleEngine.createParticle(
                    IsmParticles.VISION_PARTICLE,
                    particlePos.x, particlePos.y + player.random.nextDouble() - 0.5, particlePos.z,
                    0.0, 0.0, 0.0
                )

                if (player.random.nextDouble() < 0.02) {
                    SpookyVisions.showVision(SpookyVisions.CROWNING, 0.45f)
                } else if (player.random.nextDouble() < 0.05) {
                    SpookyVisions.showVision(SpookyVisions.OUTSIDE, 0.6f)
                }

                visionChance = 0.0
            } else {
                visionChance += 0.00015
            }
        }

        RollingFogRenderer.tick()
    }

    fun updateFog(fog: FogState) {
        val player = Minecraft.getInstance().player ?: throw IllegalStateException()
        val playerPos = player.position().toVector3d()

        if (fog.position != null && fog.distanceTo(playerPos) < FOG_DISTANCE) {
            if (lastFog == null) {
                player.playSound(IsmSounds.LAUGHS)
            }

            lastFog = fog
            RollingFogRenderer.updateRenderer(fog, playerPos)
        } else {
            lastFog = null
            RollingFogRenderer.shouldRender = false
        }
    }

    private fun RollingFogRenderer.updateRenderer(fog: FogState, player: Vector3d) {
        val pos = fog.position ?: throw IllegalStateException()
        shouldRender = true
        nextTick = {
            val diff = player.sub(pos, Vector3d())
            val hDepth = fog.thickness / 2
            val flatNormal = fog.getFlatNormal()

            height = fog.height
            velocity = fog.velocity
            lastPosition = position

            val distanceToInnerPlane = flatNormal.dot(diff)
            if (distanceToInnerPlane > 0) {
                position = Vector3d(pos).add(flatNormal.mul(hDepth))
                direction = fog.direction
            } else {
                position = Vector3d(pos).add(flatNormal.mul(-hDepth))
                direction = fog.direction.negate(Vector3f())
                direction.y = fog.direction.y
                direction.normalize()
            }
        }
    }
}