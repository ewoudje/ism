package com.ewoudje.ism.features.tristitia.poi;

import org.joml.Vector3dc;
import org.jspecify.annotations.Nullable;

import java.util.Set;
import java.util.function.Supplier;

public interface TristitiaPOI {

    Vector3dc center();

    double reach();
    boolean consumeEnergy(int requested);
    int availableEnergy();


    TristitiaPOI createChild(Supplier<TristitiaPOI> child);
    Set<TristitiaPOI> children();
    @Nullable TristitiaPOI parent();

    default TristitiaRootPOI getRoot() {
        TristitiaPOI parent = this.parent();
        if (parent == null)
            throw new IllegalStateException("TristitiaPOI no parent but isnt a closest?");

        return parent.getRoot();
    }
}
