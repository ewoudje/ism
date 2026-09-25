package com.ewoudje.ism.features.tristitia;

import java.util.Set;

public interface TristitiaPOI {

    float density();

    int retrieveEnergy(int requested);

    Set<TristitiaPOI> children();
    TristitiaPOI parent();
}
