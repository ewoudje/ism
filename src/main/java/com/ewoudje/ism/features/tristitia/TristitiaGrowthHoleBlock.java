package com.ewoudje.ism.features.tristitia;

import com.ewoudje.ism.collections.IsmBlocks;
import com.ewoudje.ism.util.block.BlockWithEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class TristitiaGrowthHoleBlock extends TristitiaGrowthBlock implements BlockWithEntity<TristitiaGrowthHoleBlockEntity> {
    public TristitiaGrowthHoleBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (level.getBlockState(pos.relative(dir)).is(IsmBlocks.SEAL_FILLING.get())){
                level.setBlock(pos.relative(dir.getOpposite()), IsmBlocks.TRISTITIA_VINE_GROWTH.get().defaultBlockState(), UPDATE_NEIGHBORS | UPDATE_CLIENTS);
            }
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new TristitiaGrowthHoleBlockEntity(worldPosition, blockState);
    }
}
