package com.ewoudje.ism.util.block;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityInvalidationListener;

import java.util.HashMap;
import java.util.Map;

public class BlockEntityCapabilitiesCache<T, C> {
    private final BlockCapability<T, C> type;
    private final ServerLevel level;
    private final Map<BlockPos, CacheEntry> cache = new HashMap<>();

    public BlockEntityCapabilitiesCache(
            BlockCapability<T, C> capability,
            ServerLevel level
    ) {
        this.type = capability;
        this.level = level;
    }

    private class CacheEntry implements ICapabilityInvalidationListener {
        private final C context;
        private final BlockEntity associatedBE;
        private boolean isInvalid = true;
        private T current;

        private CacheEntry(C context, BlockEntity associatedBE) {
            this.context = context;
            this.associatedBE = associatedBE;
        }

        @Override
        public boolean onInvalidate() {
            isInvalid = true;
            boolean isBeRemoved = associatedBE.isRemoved();
            if (isBeRemoved) {
                cache.remove(associatedBE.getBlockPos());
            }

            return isBeRemoved;
        }

        public T getCapability() {
            if (isInvalid) {
                current = level.getCapability(
                        type,
                        associatedBE.getBlockPos(),
                        associatedBE.getBlockState(),
                        associatedBE,
                        context
                );
                isInvalid = false;
            }

            return current;
        }
    }
}
