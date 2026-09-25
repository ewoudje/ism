package com.ewoudje.ism.features.tristitia;

import com.ewoudje.ism.collections.IsmAttachments;
import com.ewoudje.ism.features.tristitia.poi.TristitiaRootPOI;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.joml.Vector3d;
import org.jspecify.annotations.Nullable;

public record TristitiaGrowthSample(double density, TristitiaRootPOI root) {
    public static @Nullable TristitiaGrowthSample sample(ServerLevel level, BlockPos pos) {
        return level.getData(IsmAttachments.TRISTITIA_GROWTH_STATE).sample(
                new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)
        );
    }
}
