package com.ewoudje.ism.features.tristitia.client;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;

import java.util.List;

public record ClingyGrowthUnbakedModel(List<BlockStateModel.Unbaked> faces) implements CustomUnbakedBlockStateModel {

    @Override
    public MapCodec<? extends CustomUnbakedBlockStateModel> codec() {
        return null;
    }

    @Override
    public BlockStateModel bake(ModelBaker modelBakery) {
        return null;
    }

    @Override
    public void resolveDependencies(Resolver resolver) {

    }
}
