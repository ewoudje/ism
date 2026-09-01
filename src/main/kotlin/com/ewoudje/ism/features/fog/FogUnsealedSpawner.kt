package com.ewoudje.ism.features.fog

import com.ewoudje.ism.world.IsmWorldState.Companion.ismWorldState
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import org.joml.Vector3d
import org.joml.Vector3f
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.minus

class FogUnsealedSpawner private constructor(val players: List<Player>, val center: BlockPos) {

    fun tick(level: ServerLevel) {
        players.forEach {
            if (!it.isRemoved) {
                val center = center.center
                if (it.distanceToSqr(center) > (TRIGGER_DISTANCE * TRIGGER_DISTANCE)) {
                    val diff = it.position().minus(center)
                    val fog = level.ismWorldState.fogState
                    val direction = Vector3f(diff.x.toFloat(), 0.0f, diff.z.toFloat()).normalize()
                    fog.velocity = direction.mul(0.15f, Vector3f())
                    fog.position = Vector3d(direction.x.toDouble() * -512, 192.0, direction.z.toDouble() * -512)
                        .add(center.x, 0.0, center.z)

                    direction.y = 0.5f
                    fog.direction = direction.normalize()
                    fog.thickness = 256.0
                    spawner = null
                    return
                }
            }
        }
    }

    companion object {
        val TRIGGER_DISTANCE = 72
        var spawner: FogUnsealedSpawner? = null

        fun setupUnsealingFog(level: ServerLevel, center: BlockPos, range: Double) {
            val range2 = range * range
            val center2 = center.center
            val players = level.getPlayers { it.distanceToSqr(center2) < range2}
            spawner = FogUnsealedSpawner(players, center)
        }
    }
}