package com.ewoudje.ism.features.growth;

import com.ewoudje.ism.features.tristitia.TristitiaGrowthBlock;

public record GrowingProposal(
        TristitiaGrowthBlock block,
        int energyConsumption
) {
}
