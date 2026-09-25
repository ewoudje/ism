package com.ewoudje.ism.util.block;

import com.ewoudje.ism.util.data.SillyMap;
import com.ewoudje.ism.util.server.ServerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityInvalidationListener;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class BlockEntityTrackedCapabilities<T, C> {
    private static final Map<BlockCapability<?, ?>, BlockEntityTrackedCapabilities<?, ?>> trackedCapabilities = new HashMap<>();
    private final BlockCapability<T, C> type;
    private final Map<ServerLevel, LevelTracked> perLevel = new HashMap<>();

    private BlockEntityTrackedCapabilities(
            BlockCapability<T, C> capability
    ) {
        this.type = capability;
    }

    @SuppressWarnings("unchecked")
    public static <T, C> BlockEntityTrackedCapabilities<T, C> get(BlockCapability<T, C> capability) {
        return (BlockEntityTrackedCapabilities<T, C>) trackedCapabilities.computeIfAbsent(
                capability, BlockEntityTrackedCapabilities::new
        );
    }

    public static <T, C> BlockEntityTrackedCapabilities<T, C>.LevelTracked get(BlockCapability<T, C> capability, ServerLevel level) {
        return get(capability).forLevel(level);
    }

    public LevelTracked forLevel(ServerLevel level) {
        return perLevel.computeIfAbsent(level, LevelTracked::new);
    }

    public static <C> void trackBlockEntity(BlockCapability<?, C> capability, BlockEntity be, @Nullable C context) {
        if (!be.hasLevel()) throw new IllegalArgumentException("BlockEntity has no level yet?");
        get(capability).forLevel(ServerUtil.getLevel(be.getLevel()))
                .newBlockEntity(be, context);
    }

    public class LevelTracked {
        private final ServerLevel level;
        private final Map<BlockPos, Map<C, CacheEntry>> activeBlockEntities = new HashMap<>();

        private LevelTracked(ServerLevel level) {
            this.level = level;
        }

        public @Nullable BlockEntityCapabilityResult<T, C> find(BlockPos pos, C context) {
            var result = activeBlockEntities.get(pos);
            return result == null ? null : result.get(context);
        }

        private BlockEntityCapabilityResult<T, C> newBlockEntity(BlockEntity blockEntity, C context) {
            var entry = new CacheEntry(context, blockEntity);
            if (context == null) {
                if (activeBlockEntities.put(blockEntity.getBlockPos(), new SillyMap<>(null, entry)) != null)
                    throw new IllegalStateException();

            } else {
                Map<C, CacheEntry> entries = activeBlockEntities.computeIfAbsent(
                        blockEntity.getBlockPos(),
                        p -> new HashMap<>()
                );

                if (entries.put(context, entry) != null)
                    throw new IllegalStateException();
            }

            return entry;
        }

        public Stream<BlockEntityCapabilityResult<T, C>> stream(C context) {
            return activeBlockEntities.values().stream()
                    .flatMap(m -> m.values().stream());
        }

        private class CacheEntry implements ICapabilityInvalidationListener, BlockEntityCapabilityResult<T, C> {
            private final C context;
            private final BlockEntity associatedBE;
            private boolean isInvalid = true;
            private T current;

            private CacheEntry(C context, BlockEntity associatedBE) {
                this.context = context;
                this.associatedBE = associatedBE;
                level.registerCapabilityListener(associatedBE.getBlockPos(), this);
            }

            @Override
            public boolean onInvalidate() {
                isInvalid = true;
                boolean isBeRemoved = associatedBE.isRemoved();
                if (isBeRemoved) {
                    activeBlockEntities.remove(associatedBE.getBlockPos());
                }

                return !isBeRemoved;
            }
            @Override
            public C context() {
                return context;
            }

            @Override
            public T capability() {
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

            @Override
            public BlockEntity blockEntity() {
                return associatedBE;
            }
        }
    }
}
