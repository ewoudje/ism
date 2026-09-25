package com.ewoudje.ism.util.world;

import com.ewoudje.ism.mixins.accessors.client.ClientLevelAccessor;
import net.minecraft.client.Minecraft;

public class ClientBiomeUtil {
    public static void markBiomeSectionDirty(int sX, int sY, int sZ) {
        var tints = ((ClientLevelAccessor) Minecraft.getInstance().level).getTintCaches();

        for (var cache : tints.values()) {
            cache.invalidateForChunk(sX, sZ);
        }

        for (int x = sX - 1; x <= sX + 1; x++) {
            for (int y = sY - 1; y <= sY + 1; y++) {
                for (int z = sZ - 1; z <= sZ + 1; z++) {
                    Minecraft.getInstance().levelExtractor.setSectionDirty(x, y, z);
                }
            }
        }
    }
}
