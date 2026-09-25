package com.ewoudje.ism.util.block;

import net.minecraft.world.level.block.entity.BlockEntity;

public interface BlockEntityCapabilityResult<T, C> {
    C context();
    T capability();
    BlockEntity blockEntity();
}
