package com.ewoudje.ism.features.tristitia.poi;

import org.jspecify.annotations.Nullable;

public interface TristitiaRootPOI extends TristitiaPOI {

    float aggressiveness();

    @Override
    default @Nullable TristitiaPOI parent() {
        return null;
    }

    @Override
    default TristitiaRootPOI getRoot() {
        return this;
    }
}
