package com.ewoudje.ism.features.tristitia.client;

import com.ewoudje.ism.features.tristitia.TristitiaClingyGrowthBlock;
import com.ewoudje.ism.util.block.client.XTopConfiguration;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.neoforged.neoforge.client.model.block.CustomBlockModelDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public record ClingyGrowthModelDefinition(List<Material> textures, Material particle, boolean transparent, Optional<XTopConfiguration> xTop) implements CustomBlockModelDefinition {
    public static final MapCodec<ClingyGrowthModelDefinition> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Material.CODEC.listOf().fieldOf("materials").forGetter(ClingyGrowthModelDefinition::textures),
            Material.CODEC.fieldOf("particle").forGetter(ClingyGrowthModelDefinition::particle),
            Codec.BOOL.optionalFieldOf("transparent", false).forGetter(ClingyGrowthModelDefinition::transparent),
            XTopConfiguration.CODEC.optionalFieldOf("x-top").forGetter(ClingyGrowthModelDefinition::xTop)
    ).apply(i, ClingyGrowthModelDefinition::new));

    @Override
    public Map<BlockState, BlockStateModel.UnbakedRoot> instantiate(StateDefinition<Block, BlockState> states, Supplier<String> sourceSupplier) {
        Map<BlockState, BlockStateModel.UnbakedRoot> result = new HashMap<>();

        if (!(states.getOwner() instanceof TristitiaClingyGrowthBlock block))
            throw new IllegalStateException("Cannot instantiate ClingyGrowthModelDefinition, with a non ClingyGrowthBlock");

        Map<Direction, ClingyGrowthFace> faces =
                Maps.asMap(Set.of(Direction.values()), d -> new ClingyGrowthFace(transparent, d, block.thickness()));

        states.getPossibleStates().forEach(state -> {
            result.put(state, new ClingyGrowthUnbakedModel(
                    block.getFaces(state).map(faces::get).toList(),
                    textures,
                    particle,
                    block.thickness(),
                    xTop().orElse(null)
            ).asRoot());
        });

        return result;
    }

    @Override
    public MapCodec<? extends CustomBlockModelDefinition> codec() {
        return MAP_CODEC;
    }
}
