package com.ewoudje.ism.util.block.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.resources.model.sprite.Material;

import java.util.List;

public record XTopConfiguration(List<Material> materials, float height) {
    public static final Codec<XTopConfiguration> CODEC = RecordCodecBuilder.create(i -> i.group(
            Material.CODEC.listOf().fieldOf("materials").forGetter(XTopConfiguration::materials),
            Codec.FLOAT.fieldOf("height").forGetter(XTopConfiguration::height)
    ).apply(i, XTopConfiguration::new));
}
