package com.ewoudje.ism.features.tristitia.poi;

import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public abstract class AbstractTristitiaChildPOI implements TristitiaPOI {
    private final TristitiaRootPOI root;
    private final TristitiaPOI parent;
    private final Set<TristitiaPOI> children = new HashSet<>();
    private final Vector3d position;

    protected AbstractTristitiaChildPOI(TristitiaPOI parent, Vector3d position) {
        this.parent = parent;
        this.position = position;
        this.root = parent.getRoot();
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
        return root.consumeEnergy(requested);
    }

    @Override
    public int availableEnergy() {
        return root.availableEnergy();
    }

    @Override
    public Set<TristitiaPOI> children() {
        return children;
    }

    @Override
    public @Nullable TristitiaPOI parent() {
        return parent;
    }

    @Override
    public TristitiaRootPOI getRoot() {
        return root;
    }
}
