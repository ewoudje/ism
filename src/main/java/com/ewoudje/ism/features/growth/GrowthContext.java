package com.ewoudje.ism.features.growth;

import com.ewoudje.ism.features.tristitia.growth.TristitiaGrowthSample;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

// Extends BlockGetter so that it can cache
public interface GrowthContext extends BlockGetter {
    ServerLevel level();
    BlockPos pos();
    BlockState state();
    RandomSource random();
    @Nullable TristitiaGrowthSample sample();
    int nextTickSpeed();
    @Nullable GrowingProposal proposal();
    @Nullable GrowingProposal selfProposal();
    @Nullable BlockPos growthTarget();
}
