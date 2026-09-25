package com.ewoudje.ism.features.tristitia.client;

import com.mojang.blaze3d.platform.Transparency;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ClingyGrowthXTop implements BlockStateModelPart {
    private final List<BakedQuad> quads = new ArrayList<>();
    private final Material.Baked material;
    private final BakedQuad.MaterialInfo texture;

    public ClingyGrowthXTop(
            BlockModelRotation rotation,
            Material.Baked material,
            float offset,
            float height,
            RandomSource random
    ) {
        this.material = material;
        texture = BakedQuad.MaterialInfo.of(
                material,
                Transparency.TRANSPARENT,
                0,
                null,
                0,
                false
        );
        float left = random.nextFloat() * 4.8f;
        float right = 11.2f + left;
        float down = random.nextFloat() * 4.8f;
        float up = 11.2f + down;


        bakeFaceXTop(
                rotation,
                left, down, offset,
                right, up, offset + height
        );

        bakeFaceXTop(
                rotation,
                left, up, offset,
                right, down, offset + height
        );
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable Direction direction) {
        if (direction == null) return this.quads;
        return List.of();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return texture.ambientOcclusion();
    }

    @Override
    public Material.Baked particleMaterial() {
        return material;
    }

    @Override
    public @BakedQuad.MaterialFlags int materialFlags() {
        return texture.flags();
    }

    private void bakeFaceXTop(
            BlockModelRotation rotation,
            float x1, float y1, float z1,
            float x2, float y2, float z2
    ) {
        x1 = Math.clamp(x1, 2.5f, 13.5f);
        y1 = Math.clamp(y1, 2.5f, 13.5f);
        x2 = Math.clamp(x2, 2.5f, 13.5f);
        y2 = Math.clamp(y2, 2.5f, 13.5f);

        x1 /= 16f;
        y1 /= 16f;
        z1 /= 16f;
        x2 /= 16f;
        y2 /= 16f;
        z2 /= 16f;
        var matrix = rotation.transformation().getMatrix();
        var sprite = texture.sprite();
        var p1 = transform(matrix, new Vector3f(x1, y1, z2));
        var p2 = transform(matrix, new Vector3f(x1, y1, z1));
        var p3 = transform(matrix, new Vector3f(x2, y2, z1));
        var p4 = transform(matrix, new Vector3f(x2, y2, z2));
        var uv1 = UVPair.pack(sprite.getU(0), sprite.getV(0));
        var uv2 = UVPair.pack(sprite.getU(0), sprite.getV(1));
        var uv3 = UVPair.pack(sprite.getU(1), sprite.getV(1));
        var uv4 = UVPair.pack(sprite.getU(1), sprite.getV(0));

        quads.add(new BakedQuad(
                p4, p3, p2, p1,
                uv4, uv3, uv2, uv1,
                Direction.UP,
                texture
        ));
        quads.add(new BakedQuad(
                p1, p2, p3, p4,
                uv1, uv2, uv3, uv4,
                Direction.DOWN,
                texture
        ));
    }

    private Vector3f transform(Matrix4fc m, Vector3f v) {
        return m.transformPosition(v.sub(0.5f, 0.5f, 0.5f)).add(0.5f, 0.5f, 0.5f);
    }
}
