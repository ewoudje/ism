package com.ewoudje.ism.features.seal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SealStoneBlock extends Block {

    public SealStoneBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos nPos = pos.relative(direction);
            BlockState neighbor = level.getBlockState(nPos);
            if (neighbor.getBlock() instanceof SealFillingBlock filler) {
                level.setBlock(pos, filler.fillHoleWith(level, nPos, pos, neighbor, state), UPDATE_NEIGHBORS | UPDATE_CLIENTS);
            }
        }
    }
}
