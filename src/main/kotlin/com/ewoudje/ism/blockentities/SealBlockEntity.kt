package com.ewoudje.ism.blockentities

import com.ewoudje.ism.client.IsmSounds
import com.ewoudje.ism.client.particles.IsmParticles
import com.ewoudje.ism.client.particles.UnsealParticleProvider
import com.ewoudje.ism.world.IsmWorldState.Companion.ismWorldState
import com.ewoudje.ism.features.fog.FogUnsealedSpawner
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Holder
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientboundSoundPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.minus
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.times

class SealBlockEntity(pos: BlockPos, state: BlockState) : SyncedBlockEntity(IsmBlockEntities.SEAL, pos, state) {
    private var opened = false
    private var openingProgress = -1

    override fun readData(tag: CompoundTag) {
        opened = tag.getBoolean("opened")
    }

    override fun writeData(tag: CompoundTag) {
        tag.putBoolean("opened", opened)
    }

    fun checkHackers(): Boolean {
        if (isOpen()) return false
        val level = level ?: throw IllegalStateException()

        for (dir in arrayOf(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST)) {
            val pos = blockPos.offset(dir.normal * 3)
            val blockEntity = level.getBlockEntity(pos) as? SealHackerBlockEntity ?: return false
            if (!blockEntity.isSlotted()) return false
        }

        return true
    }

    fun isOpening(): Boolean = openingProgress >= 0
    fun isOpen(): Boolean = opened
    fun scaledProgress(partialTick: Float, min: Float, max: Float): Float =
        if (isOpen())
            1f
        else if (isOpening())  {
            val progress = 1 - ((openingProgress.toFloat() - partialTick) / TOTAL_OPENING_TIME)
            val clamped = Math.clamp(progress, min, max) - min
            if (clamped == 0.0f) 0.0f else clamped / (max - min)
        } else 0f

    fun open(): Boolean {
        if (isOpening()) return false
        if (isOpen()) return false
        if (!checkHackers()) return false
        if (!checkTimeWest() && !checkTimeEast()) return false
        openingProgress = TOTAL_OPENING_TIME

        val level = level!!

        level.playSound(null, blockPos, IsmSounds.SLIDING_KEY_OPEN, SoundSource.BLOCKS, 1f, 1f)

        return true
    }

    fun renderHackerParticles() {
        if (isOpen()) return
        val level = level ?: return
        if (!level.isClientSide) return
        level as ClientLevel

        if (Minecraft.getInstance().player!!.distanceToSqr(this.blockPos.center) > 64*64) return

        fun addParticle(x: Double, z: Double) =
            level.addParticle(
                IsmParticles.UNSEAL_PARTICLE,
                x + this.blockPos.x + 0.5,
                this.blockPos.y.toDouble() + 0.1,
                z + this.blockPos.z + 0.5,
                this.blockPos.x + 0.5,
                this.blockPos.y + 0.5,
                this.blockPos.z + 0.5
            )

        for (i in 0..PARTICLE_RESOLUTION) {
            val angle = i * Math.PI * 2.0 / PARTICLE_RESOLUTION
            if (i % 25 == 0) UnsealParticleProvider.setString("________DONT_UNSEAL______")

            val x = Math.cos(angle) * 3
            val z = Math.sin(angle) * 3

            addParticle(x, z)
        }


    }

    fun renderMoonParticles() {
        val level = level ?: return
        if (!level.isClientSide) return
        if (!checkTimeWest() && !checkTimeEast()) return
        level as ClientLevel

        val windowPos = this.blockPos.offset(
            if (checkTimeWest()) -6 else 6,
            6,
            0
        ).bottomCenter
        val orig = this.blockPos.center
        val diff = (windowPos - orig).normalize().scale(10.0)

        repeat(5) {
            val pos = orig.lerp(windowPos, it / 5.0)
            level.addParticle(
                ParticleTypes.ENCHANT,
                pos.x + if (checkTimeWest()) 1.5 else -1.5,
                pos.y,
                pos.z,
                diff.x,
                diff.y,
                diff.z
            )
        }
    }

    private fun checkTimeEast(): Boolean =
        (level!!.dayTime % 24000) in 14900..15300

    private fun checkTimeWest(): Boolean =
        (level!!.dayTime % 24000) in 20700..21100


    companion object {
        val TOTAL_OPENING_TIME = 150
        val PARTICLE_RESOLUTION = 100

        fun tick(level: Level, pos: BlockPos, state: BlockState, self: SealBlockEntity) {
            if (self.isOpening()) {
                self.openingProgress--
                if (self.openingProgress <= 0) {
                    self.opened = true
                    self.openingProgress = -1
                    self.setChanged()

                    if (!level.isClientSide) {
                        level as ServerLevel
                        level.ismWorldState.isEvilGodFree = true
                        FogUnsealedSpawner.setupUnsealingFog(level, pos, 64.0)
                    }
                } else if (self.openingProgress == 20 && !level.isClientSide) {
                    level as ServerLevel
                    level.server.playerList.broadcastAll(
                        ClientboundSoundPacket(
                            Holder.direct(IsmSounds.GOD_UNSEALED),
                            SoundSource.AMBIENT,
                            pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(),
                            1f, 1f,
                            0)
                    )
                }

                if (level.isClientSide) {
                    self.renderHackerParticles()
                    self.renderMoonParticles()
                }
            } else {
                if (level.isClientSide && self.checkHackers()) {
                    self.renderHackerParticles()
                }

                if (level.isClientSide && (self.checkTimeEast() || self.checkTimeWest())) {
                    self.renderMoonParticles()
                }
            }
        }

    }
}