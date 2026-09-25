package com.ewoudje.ism.features.tristitia.client;

import com.ewoudje.ism.util.math.FlatDirection;
import com.ewoudje.ism.util.math.RotationDirectionUtil;
import com.mojang.blaze3d.platform.Transparency;
import com.mojang.math.Quadrant;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.cuboid.CuboidFace;
import net.minecraft.client.resources.model.cuboid.FaceBakery;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.model.ExtraFaceData;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ClingyGrowthFace {
    private static final ExtraFaceData FACE_DATA = new ExtraFaceData(0xFFFFFFFF, 0, false);
    private final Direction facingDirection;
    private final BlockModelRotation rotation;
    private final float thickness;
    private final boolean transparent;
    private Variant[][] variants;
    private Material.@Nullable Baked particle = null;

    public ClingyGrowthFace(boolean transparent, Direction facingDirection, float thickness) {
        this.facingDirection = facingDirection;
        this.transparent = transparent;
        this.thickness = thickness;

        rotation = RotationDirectionUtil.direction2bmRotation(facingDirection);
    }

    public Direction direction() {
        return facingDirection;
    }

    public void configure(ModelBaker modelBaker, List<Material.Baked> textures, Material.Baked particle) {
        if (this.particle != null) return;
        this.particle = particle;

        variants = new Variant[textures.size()][256];

        for (int t = 0; t < textures.size(); t++) {
            var materialInfo = BakedQuad.MaterialInfo.of(
                    textures.get(t),
                    transparent ? Transparency.TRANSPARENT : Transparency.NONE,
                    0,
                    null,
                    0,
                    false
            );

            for (int i = 0; i < 256; i++) {
                final int forShift = i;
                variants[t][i] = new Variant(
                        modelBaker,
                        materialInfo,
                        dir -> {
                            int typeOrdinal = (forShift >> (dir.ordinal() * 2)) & 3;
                            return ConnectionType.values()[typeOrdinal];
                        }
                );
            }
        }

    }

    public Variant pickVariant(RandomSource random, int flags) {
        return pickVariant(random.nextInt(variants.length), flags);
    }

    public Variant pickVariant(int variant, int flags) {
        return variants[variant][flags];
    }

    public class Variant implements BlockStateModelPart {
        private final List<BakedQuad> quads = new ArrayList<>();
        private final BakedQuad.MaterialInfo materialInfo;

        private Variant(ModelBaker baker, BakedQuad.MaterialInfo info, Function<FlatDirection, ConnectionType> connections) {
            this.materialInfo = info;
            ConnectionType leftType = connections.apply(FlatDirection.LEFT);
            ConnectionType rightType = connections.apply(FlatDirection.RIGHT);
            ConnectionType upType = connections.apply(FlatDirection.UP);
            ConnectionType downType = connections.apply(FlatDirection.DOWN);

            float left = 16f - leftType.coordinate(thickness);
            float right = rightType.coordinate(thickness);
            float up = upType.coordinate(thickness);
            float down = 16f - downType.coordinate(thickness);

            bakeFace(
                    baker,
                    left, down, thickness,
                    right, up, thickness,
                    left, down,
                    right, up,
                    Direction.SOUTH
            );

            if (!transparent)
                bakeSideFaces(baker, left, right, up, down);
        }

        private void bakeSideFaces(ModelBaker baker, float left, float right, float up, float down) {
            bakeFace(
                    baker,
                    left, up - 0.01f, 0,
                    right, up - 0.01f, thickness,
                    left, 0,
                    right, thickness,
                    Direction.UP
            );

            bakeFace(
                    baker,
                    left, down - 0.01f, 0,
                    right, down - 0.01f, thickness,
                    left, 0,
                    right, thickness,
                    Direction.DOWN
            );

            bakeFace(
                    baker,
                    left - 0.01f, down, 0,
                    left - 0.01f, up, thickness,
                    thickness, down,
                    thickness, up,
                    Direction.WEST
            );

            bakeFace(
                    baker,
                    right - 0.01f, down, 0,
                    right - 0.01f, up, thickness,
                    thickness, down,
                    thickness, up,
                    Direction.EAST
            );
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable Direction direction) {
            if (direction == null) return quads;
            return List.of();
        }

        @Override
        public boolean useAmbientOcclusion() {
            return materialInfo.ambientOcclusion();
        }

        @Override
        public Material.Baked particleMaterial() {
            return particle;
        }

        @Override
        public @BakedQuad.MaterialFlags int materialFlags() {
            return materialInfo.flags();
        }
        private void bakeFace(
                ModelBaker baker,
                float x, float y, float z,
                float x2, float y2, float z2,
                float u, float v, float u2, float v2,
                Direction dir
        ) {
            quads.add(FaceBakery.bakeQuad(
                    baker.interner(),
                    new Vector3f(x, y, z),
                    new Vector3f(x2, y2, z2),
                    new CuboidFace.UVs(u + thickness, v + thickness, u2, v2),
                    Quadrant.R0,
                    materialInfo,
                    dir,
                    rotation,
                    null,
                    FACE_DATA
            ));
        }
    }

    public enum ConnectionType {
        NONE,
        NORMAL,
        SHORT,
        LONG;

        public float coordinate(float thickness) {
            return switch (this) {
                case NONE -> 14F;
                case NORMAL -> 16F;
                case SHORT -> 16F - thickness;
                case LONG -> 16F + thickness;
            };
        }
    }
}
