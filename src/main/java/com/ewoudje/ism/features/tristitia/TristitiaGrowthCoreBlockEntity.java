package com.ewoudje.ism.features.tristitia;

import com.ewoudje.ism.collections.IsmBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TristitiaGrowthHoleBlockEntity extends BlockEntity {
    public TristitiaGrowthHoleBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(IsmBlockEntities.TRISTITIA_SEAL_HOLE.get(), worldPosition, blockState);
    }
}
