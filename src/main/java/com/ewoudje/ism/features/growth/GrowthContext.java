package com.ewoudje.ism.features.tristitia.growth;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public interface GrowthContext {

    ServerLevel level();
    BlockPos pos();
    BlockState state();
    RandomSource random();
    TristitiaGrowthSample sample();
}
