package com.ewoudje.ism.features.tristitia.client;

import com.ewoudje.ism.features.tristitia.TristitiaClingyGrowthBlock;
import com.ewoudje.ism.util.math.FlatDirection;
import com.ewoudje.ism.util.math.RotationDirectionUtil;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ClingyGrowthModel implements DynamicBlockStateModel {
    private final Material.Baked particle;
    private final List<ClingyGrowthFace> faces;
    private final List<FlatDirection>[] directionsToCheck = new List[6];
    private final float thickness;
    private final int[] premadeFlags = new int[6];
    private final int materialFlags;
    private final @Nullable List<Material.Baked> xTopTextures;
    private final float xTopHeight;

    public ClingyGrowthModel(List<ClingyGrowthFace> faces, Material.Baked particle, float thickness, @Nullable List<Material.Baked> xTopTextures, float xTopHeight) {
        this.particle = particle;
        this.faces = faces;
        this.thickness = thickness;
        this.xTopTextures = xTopTextures;
        this.xTopHeight = xTopHeight;

        for (ClingyGrowthFace face : faces) {
            int dirIdx = face.direction().ordinal();
            int dirFlags = 0;
            List<FlatDirection> list = new ArrayList<>();

            for (FlatDirection dir : FlatDirection.values()) {
                Direction combinedDir = dir.moveDirection(face.direction());
                if (faces.stream().noneMatch(f -> f.direction() == combinedDir)) {
                    list.add(dir);
                } else {
                    int f = face.direction().get2DDataValue() == -1 //TODO merge into face calc?
                            ? ClingyGrowthFace.ConnectionType.NORMAL.ordinal()
                            : ClingyGrowthFace.ConnectionType.SHORT.ordinal();

                    dirFlags |= f << (dir.ordinal() * 2);
                }
            }

            directionsToCheck[dirIdx] = list;
            premadeFlags[dirIdx] = dirFlags;
        }

        materialFlags = faces.stream().findAny().map(f -> f.pickVariant(0, 0).materialFlags()).orElse(0);
    }

    @Override
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
        for (ClingyGrowthFace face : faces) {
            int flags = buildFlags(level, pos, face.direction());
            parts.add(face.pickVariant(random, flags));

            if (xTopTextures != null
                    && face.direction().get2DDataValue() == -1
                    && random.nextBoolean()) {

                parts.add(new ClingyGrowthXTop(
                        RotationDirectionUtil.direction2bmRotation(face.direction()),
                        xTopTextures.get(random.nextInt(xTopTextures.size())),
                        thickness,
                        xTopHeight,
                        random
                ));
            }
        }
    }

    private int buildFlags(BlockAndTintGetter level, BlockPos pos, Direction face) {
        int flags = premadeFlags[face.ordinal()];
        for (FlatDirection dir : directionsToCheck[face.ordinal()]) {
            if (TristitiaClingyGrowthBlock.isFaceFlatConnected(level, pos, face, dir)) {
                flags |= ClingyGrowthFace.ConnectionType.NORMAL.ordinal() << (dir.ordinal() * 2);
            } else if (TristitiaClingyGrowthBlock.isFaceCornerConnected(level, pos, face, dir, thickness)) {
                int f = face.get2DDataValue() == -1 || dir.isHorizonal()  //TODO merge into face calc?
                        ? ClingyGrowthFace.ConnectionType.LONG.ordinal()
                        : ClingyGrowthFace.ConnectionType.NORMAL.ordinal();

                flags |= f << (dir.ordinal() * 2);
            }
        }

        return flags;
    }

    @Override
    public Material.Baked particleMaterial() {
        return particle;
    }

    @Override
    public @BakedQuad.MaterialFlags int materialFlags() {
        return materialFlags;
    }
}
