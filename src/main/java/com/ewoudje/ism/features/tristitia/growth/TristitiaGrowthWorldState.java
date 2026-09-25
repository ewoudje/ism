package com.ewoudje.ism.features.tristitia;

import com.ewoudje.ism.features.tristitia.poi.TristitiaPOI;
import com.ewoudje.ism.features.tristitia.poi.TristitiaRootPOI;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import org.joml.Vector3dc;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class TristitiaGrowthWorldState {
    private final ServerLevel level;
    private final Set<TristitiaPOI> pois = new HashSet<>();

    public TristitiaGrowthWorldState(IAttachmentHolder holder) {
        if (!(holder instanceof ServerLevel level))
            throw new IllegalArgumentException();

        this.level = level;
    }

    public @Nullable TristitiaGrowthSample sample(Vector3dc pos) {
        int hits = 0;
        double totalDensity = 0;
        double closestDistance = Double.MAX_VALUE;
        TristitiaPOI closestPOI = null;
        for (TristitiaPOI poi : pois) {
            if (Double.isNaN(poi.density())) continue;
            double distance = pos.distance(poi.center());
            if (distance > poi.density()) continue;

            hits++;
            totalDensity += poi.density() - distance;

            if (distance < closestDistance) {
                closestDistance = distance;
                closestPOI = poi;
            }
        }

        if (closestPOI == null) return null;
        return new TristitiaGrowthSample(
            totalDensity / hits,
                closestPOI.getRoot()
        );
    }

}
