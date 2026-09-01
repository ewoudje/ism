package com.ewoudje.ism

import com.ewoudje.ism.blocks.IsmBlocks
import com.ewoudje.ism.blockentities.IsmBlockEntities
import com.ewoudje.ism.client.SpookyVisions
import com.ewoudje.ism.items.IsmItems
import com.ewoudje.ism.networking.IsmPackets
import com.ewoudje.ism.world.IsmWorldHandler
import com.ewoudje.ism.world.IsmWorldState.Companion.ismWorldState
import com.ewoudje.ism.features.fog.FogFeature
import com.ewoudje.ism.features.shizo.ShizoFeature
import com.ewoudje.ism.world.structures.IsmStructurePieces
import com.ewoudje.ism.world.structures.IsmStructures
import com.ewoudje.ism.world.structures.processor.IsmStructureProcessors
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.registries.NewRegistryEvent
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3f
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVector3d

@Mod(IsmMod.ID)
object IsmMod {
    const val ID = "ism"

    // the logger for our mod
    val LOGGER: Logger = LogManager.getLogger(ID)

    init {
        IsmBlocks.REGISTRY.register(MOD_BUS)
        IsmPOIs.REGISTRY.register(MOD_BUS)
        IsmBlockEntities.REGISTRY.register(MOD_BUS)
        IsmAttributes.REGISTRY.register(MOD_BUS)
        IsmItems.REGISTRY.register(MOD_BUS)
        IsmStructures.PLACEMENT_REGISTRY.register(MOD_BUS)
        IsmStructures.TYPE_REGISTRY.register(MOD_BUS)
        IsmStructurePieces.REGISTRY.register(MOD_BUS)
        IsmStructureProcessors.REGISTRY.register(MOD_BUS)
        IsmEffects.REGISTRY.register(MOD_BUS)
        IsmAttachments.REGISTRY.register(MOD_BUS)

        ShizoFeature.register(MOD_BUS)
        FogFeature.register(MOD_BUS)

        MOD_BUS.addListener(IsmCapabilities::registerCapabilities)
        MOD_BUS.addListener(IsmAttributes::registerAttributes)
        MOD_BUS.addListener(IsmPackets::register)
        MOD_BUS.addListener(::registerRegistries)

        FORGE_BUS.addListener(::registerCommands)

        IsmWorldHandler.register()

        LOGGER.log(Level.INFO, "Spooky mod active!")
    }

    fun registerCommands(event: RegisterCommandsEvent) {
        event.dispatcher.register(LiteralArgumentBuilder.literal<CommandSourceStack>(ID).then(
            LiteralArgumentBuilder.literal<CommandSourceStack?>("fog").executes {
                val fog = it.source.level.ismWorldState.fogState
                fog.position = it.source.anchor.apply(it.source).toVector3d()
                fog.position!!.y = 192.0
                fog.thickness = 100.0
                fog.velocity = Vector3f(0.1f, 0f, 0f)
                fog.direction = Vector3f(0.5f, 1f, 0f).normalize()
                fog.height = (it.source.level.height + 100).toDouble()

                1
            }))
    }

    fun registerRegistries(event: NewRegistryEvent) {
        event.register(SpookyVisions.REGISTRY)
    }

    val VECTOR3D_CODEC = StreamCodec.composite(
        ByteBufCodecs.DOUBLE,
        Vector3dc::x,
        ByteBufCodecs.DOUBLE,
        Vector3dc::y,
        ByteBufCodecs.DOUBLE,
        Vector3dc::z,
        ::Vector3d
    )

    fun <T> StreamCodec<ByteBuf, T>.nullable() = object : StreamCodec<ByteBuf, T?> {
        override fun decode(buffer: ByteBuf): T? {
            val notNull = buffer.readBoolean()
            return if (notNull)
                this@nullable.decode(buffer)
            else
                null
        }

        override fun encode(buffer: ByteBuf, value: T?) {
            buffer.writeBoolean(value != null)
            if (value != null) this@nullable.encode(buffer, value)
        }
    }

}

val String.resource get() = ResourceLocation.fromNamespaceAndPath(IsmMod.ID, this)
val String.component get() = Component.translatable(this)
