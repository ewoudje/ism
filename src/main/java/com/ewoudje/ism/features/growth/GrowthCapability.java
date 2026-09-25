package com.ewoudje.ism.features.growth;

import net.minecraft.core.BlockPos;
import org.jspecify.annotations.Nullable;

public interface GrowthCapability {

    /**
    * @return -1 means that its satiated and should only get rechecked for neighbor changes
     *        otherwise it is when it should be rechecked again in ticks
    */
    int nextTrySpeed(GrowthContext ctx);

    /**
     * @return if null, then no growing is wanted
     */
    @Nullable BlockPos requestGrow(GrowthContext ctx);

    @Nullable GrowingProposal createGrowProposal(GrowthContext ctx);

    @Nullable GrowingProposal selfGrowthProposal(GrowthContext ctx);
}
