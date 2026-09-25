package com.ewoudje.ism.features.tristitia.client;

import com.ewoudje.ism.util.block.client.XTopConfiguration;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.sprite.Material;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record ClingyGrowthUnbakedModel(
        List<ClingyGrowthFace> faces,
        List<Material> materials,
        Material particle,
        float thickness,
        @Nullable XTopConfiguration xTopConfiguration
) implements CustomUnbakedBlockStateModel {
    private static final ModelDebugName DEBUG_NAME = () -> "ClingyGrowthUnbakedModel";

    @Override
    public MapCodec<? extends CustomUnbakedBlockStateModel> codec() {
        return null; //TODO
    }

    @Override
    public BlockStateModel bake(ModelBaker modelBakery) {

        var textures = bakeMaterials(modelBakery, materials());
        var xTopTextures = xTopConfiguration != null
                ? bakeMaterials(modelBakery, xTopConfiguration.materials())
                : null;

        var particle = modelBakery.materials().get(this.particle, DEBUG_NAME);

        for (var face : this.faces) {
            face.configure(modelBakery, textures, particle);
        }

        return new ClingyGrowthModel(
                faces,
                particle,
                thickness,
                xTopTextures,
                xTopConfiguration != null ? xTopConfiguration().height() : 0f
        );
    }

    @Override
    public void resolveDependencies(Resolver resolver) {

    }

    private List<Material.Baked> bakeMaterials(ModelBaker modelBakery, List<Material> materials) {
        return materials.stream()
                .map(m -> modelBakery.materials().get(m, DEBUG_NAME))
                .toList();
    }
}
