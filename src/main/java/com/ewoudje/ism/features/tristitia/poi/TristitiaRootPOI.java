package com.ewoudje.ism.features.tristitia.poi;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3dc;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public interface TristitiaPOI {

    Vector3dc center();

    double density();
    int retrieveEnergy(int requested);
    int availableEnergy();

    Set<TristitiaPOI> children();
    @Nullable TristitiaPOI parent();
}
