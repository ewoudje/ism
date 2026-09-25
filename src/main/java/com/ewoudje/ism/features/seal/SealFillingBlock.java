package com.ewoudje.ism.features.seal;

import com.ewoudje.ism.collections.IsmBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SealFillingBlock extends Block {

    public SealFillingBlock(Properties properties) {
        super(properties);
    }

    public BlockState fillHoleWith(ServerLevel level, BlockPos thisPos, BlockPos holePos, BlockState thisState, BlockState preHoleState) {
        return IsmBlocks.TRISTITIA_SEAL_HOLE.get().defaultBlockState();
    }
}
