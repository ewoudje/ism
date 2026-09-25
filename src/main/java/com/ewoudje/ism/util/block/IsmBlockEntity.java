package com.ewoudje.ism.util.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;

public class IsmBlockEntity extends BlockEntity {
    public IsmBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
    }

    protected void trackCapability(BlockCapability<?, Void> capability) {
        if (getLevel().isClientSide()) return;
        BlockEntityTrackedCapabilities.trackBlockEntity(capability, this, null);
    }
}
