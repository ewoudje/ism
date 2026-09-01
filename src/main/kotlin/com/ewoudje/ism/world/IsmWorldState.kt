package com.ewoudje.ism.world

import com.ewoudje.ism.features.fog.FogState
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.saveddata.SavedData

class IsmWorldState(
    isEvilGodFree: Boolean = false,
    val fogState: FogState = FogState(),
    val singlePlacementData: SinglePlacementData = SinglePlacementData()
) : SavedData() {
    init {
        singlePlacementData.setSetDirty(::setDirty)
        fogState.setSetDirty(::setDirty)
    }

    var isEvilGodFree = isEvilGodFree
        set(value) { field = value; setDirty() }

    override fun save(tag: CompoundTag, registries: HolderLookup.Provider): CompoundTag {
        tag.putBoolean("isEvilGodFree", isEvilGodFree)
        singlePlacementData.save(tag)
        tag.put("fogState", CompoundTag().apply { fogState.save(this) })
        return tag
    }

    companion object {
        fun create(): IsmWorldState = IsmWorldState()
        fun load(tag: CompoundTag, lookupProvider: HolderLookup.Provider): IsmWorldState = IsmWorldState(
            tag.getBoolean("isEvilGodFree"),
            FogState(tag.getCompound("fogState")),
            SinglePlacementData(tag)
        )

        val ServerLevel.ismWorldState get() =
            this.server.overworld().dataStorage.computeIfAbsent(Factory(::create, ::load), "ism_data")
    }
}