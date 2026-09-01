package com.ewoudje.ism.features.fog

import com.ewoudje.ism.IsmMod
import com.ewoudje.ism.IsmMod.nullable
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3f
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVector3d
import kotlin.math.abs

class FogState(
    position: Vector3d? = null,
    direction: Vector3f = Vector3f(),
    velocity: Vector3f = Vector3f(),
    thickness: Double = 192.0,
    height: Double = 300.0,
) {
    private lateinit var setDirty: () -> Unit
    private var networkDirty = true
    var position = position
        set(value) { field = value; setDirty() }
    var direction = direction
        set(value) { field = value; setDirty() }
    var velocity = velocity
        set(value) { field = value; setDirty() }
    var thickness = thickness
        set(value) { field = value; setDirty() }
    var height = height
        set(value) { field = value; setDirty() }

    constructor(tag: CompoundTag) : this(
        if (tag.getBoolean("visible"))
            Vector3d(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"))
        else null,
        Vector3f(tag.getFloat("dirX"), tag.getFloat("dirY"), tag.getFloat("dirZ")),
        Vector3f(tag.getFloat("velX"), tag.getFloat("velY"), tag.getFloat("velZ")),
        tag.getDouble("thickness"),
        tag.getDouble("height")
    )

    fun save(tag: CompoundTag) {
        val pos = position
        tag.putBoolean("visible", pos != null)
        if (pos != null) {
            tag.putDouble("x", pos.x)
            tag.putDouble("y", pos.y)
            tag.putDouble("z", pos.z)
        }

        tag.putFloat("dirX", direction.x)
        tag.putFloat("dirY", direction.y)
        tag.putFloat("dirZ", direction.z)

        tag.putFloat("velX", velocity.x)
        tag.putFloat("velY", velocity.y)
        tag.putFloat("velZ", velocity.z)

        tag.putDouble("thickness", thickness)
        tag.putDouble("height", height)
    }

    fun consumeNetworkDirty(): Boolean {
        val r = networkDirty
        networkDirty = false
        return r
    }

    fun setSetDirty(setDirty: () -> Unit) {
        this.setDirty = { setDirty(); this.networkDirty = true }
    }

    fun isInFog(vec: Vector3dc): Boolean {
        val pos = position ?: return false
        val flat = getFlatNormal()
        val hDepth = thickness / 2
        val side = flat.mul(hDepth)
        val dir = direction.toVector3d()
        val dir2 = dir.negate(Vector3d())
        dir2.y = dir.y

        val p1 = pos.add(side, Vector3d())
        val p2 = pos.sub(side, Vector3d())

        val d1 = vec.sub(p1, p1).dot(dir)
        val d2 = vec.sub(p2, p2).dot(dir2)

        return d1 < 0 && d2 < 0
    }

    fun getFlatNormal() = Vector3d(direction.x.toDouble(), 0.0, direction.z.toDouble()).normalize()

    fun distanceTo(pos: Vector3dc): Double {
        val n = getFlatNormal()
        return abs(pos.sub(position, Vector3d()).dot(n)) - thickness
    }

    companion object {
        val STREAM_CODEC = StreamCodec.composite(
            IsmMod.VECTOR3D_CODEC.nullable(),
            FogState::position,
            ByteBufCodecs.VECTOR3F,
            FogState::direction,
            ByteBufCodecs.VECTOR3F,
            FogState::velocity,
            ByteBufCodecs.DOUBLE,
            FogState::thickness,
            ByteBufCodecs.DOUBLE,
            FogState::height,
            ::FogState
        )
    }
}