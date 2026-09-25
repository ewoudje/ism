package com.ewoudje.ism.features.tristitia.poi;

import com.ewoudje.ism.features.tristitia.TristitiaGrowthCoreBlockEntity;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class TristitiaCorePOI implements TristitiaRootPOI {
    private final Set<TristitiaPOI> children = new HashSet<>();
    private final Vector3d position;
    private final TristitiaGrowthCoreBlockEntity be;

    public TristitiaCorePOI(TristitiaGrowthCoreBlockEntity be, Vector3d position) {
        this.position = position;
        this.be = be;
    }

    @Override
    public TristitiaPOI createChild(Supplier<TristitiaPOI> child) {
        var result = child.get();
        children.add(result);
        return result;
    }

    @Override
    public Vector3dc center() {
        return position;
    }

    @Override
    public double reach() {
        return Double.NaN;
    }

    @Override
    public boolean consumeEnergy(int requested) {
        return be.consumeEnergy(requested);
    }

    @Override
    public int availableEnergy() {
        return be.availableEnergy();
    }

    @Override
    public Set<TristitiaPOI> children() {
        return children;
    }

    @Override
    public float aggressiveness() {
        return be.aggressiveness();
    }

    @Override
    public String toString() {
        return "(c: " + position.x + ", " + position.y + ", " + position.z + ")";
    }
}
