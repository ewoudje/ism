package com.ewoudje.ism.features.tristitia.client;

import com.ewoudje.ism.features.tristitia.TristitiaGrowthBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class TristitiaGrowthCoreBlock extends TristitiaGrowthBlock {

    @Override
    public BlockState getStateForGrowth(ServerLevel level, BlockPos pos, RandomSource random) {
        return defaultBlockState();
    }
}
