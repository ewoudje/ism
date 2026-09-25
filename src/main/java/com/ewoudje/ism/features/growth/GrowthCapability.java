package com.ewoudje.ism.features.tristitia.growth;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import org.jspecify.annotations.Nullable;

public interface TristitiaGrowthCapability {

    /**
    * @return -1 means that its satiated and should only get rechecked for neighbor changes
     *        otherwise it is when it should be rechecked again in ticks
    */
    int nextTrySpeed(Context context);

    /**
     * @return if null, then no growing is wanted
     */
    @Nullable Direction requestGrow(Context context);

    @Nullable GrowingProposal createGrowProposal(Context ctx, Direction direction);

    record Context(
            ServerLevel level,
            BlockPos pos,
            BlockState state,
            RandomSource random,
            TristitiaGrowthSample sample
    ) {

    }
}
