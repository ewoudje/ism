package com.ewoudje.ism.features.tristitia.growth;

import com.ewoudje.ism.collections.IsmBlocks;
import com.ewoudje.ism.features.growth.GrowingProposal;
import com.ewoudje.ism.features.growth.GrowthCapability;
import com.ewoudje.ism.features.growth.GrowthContext;
import com.ewoudje.ism.features.tristitia.TristitiaClingyGrowthBlock;
import com.ewoudje.ism.util.math.FlatDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SimpleTristitiaGrowth implements GrowthCapability {
    public static final SimpleTristitiaGrowth INSTANCE = new SimpleTristitiaGrowth();
    private final Map<BlockState, Set<Direction>> sidesToCheck = new HashMap<>();
    private final BlockPos.MutableBlockPos scratchPos = new BlockPos.MutableBlockPos();
    private final TristitiaClingyGrowthBlock block = IsmBlocks.TRISTITIA_VINE_GROWTH.get();

    private SimpleTristitiaGrowth() {
        for (var state : block.getStateDefinition().getPossibleStates()) {
            Set<Direction> toCheck = new HashSet<>();

            // Add all possible reachable sides
            block.getFaces(state).forEach(f -> {
                for (FlatDirection flat : FlatDirection.values()) {
                    Direction checkDirection = flat.moveDirection(f);
                    toCheck.add(checkDirection);
                }
            });

            // Remove already grown sides
            block.getFaces(state).forEach(toCheck::remove);

            sidesToCheck.put(state, toCheck);
        }
    }

    @Override
    public int nextTrySpeed(GrowthContext ctx) {
        var sample = ctx.sample();
        if (sample == null) return -1;
        if (sample.density() < 1) return -1;
        if (ctx.growthTarget() == null && ctx.selfProposal() == null) return -1;
        if (sample.closest().availableEnergy() < 100) return 100;

        if (sample.density() > 70) return 15;
        return 50;
    }

    @Override
    public @Nullable BlockPos requestGrow(GrowthContext ctx) {
        var sample = ctx.sample();
        if (sample == null) return null;
        if (sample.closest().availableEnergy() < 100) return null;

        var directions = sidesToCheck.get(ctx.state());
        for (Direction direction : Direction.allShuffled(ctx.random())) {
            if (ctx.state().getValue(TristitiaClingyGrowthBlock.PROPERTY_BY_DIRECTION.get(direction))) {
                BlockPos result = checkDirection(ctx, direction, directions);
                if (result != null) return result;
            }
        }

        return null;
    }

    @Override
    public @Nullable GrowingProposal createGrowProposal(GrowthContext ctx) {
        return new GrowingProposal(IsmBlocks.TRISTITIA_VINE_GROWTH.get(), 10);
    }

    @Override
    public @Nullable GrowingProposal selfGrowthProposal(GrowthContext ctx) {
        var sample = ctx.sample();
        if (sample == null) return null;
        if (sample.density() < 60) return null;
        if (sample.closest().availableEnergy() < 500) return null;

        boolean isNextToSeal = IsmBlocks.TRISTITIA_VINE_GROWTH.get().getFaces(ctx.state()).anyMatch(d -> {
            var state = ctx.getBlockState(ctx.pos().relative(d));
            return state.is(IsmBlocks.TRISTITIA_CORE.get())
                    || state.is(IsmBlocks.TRISTITIA_SEAL_HOLE.get())
                    || state.is(IsmBlocks.SEAL_STONE.get());
        });

        if (ctx.state().is(IsmBlocks.TRISTITIA_CORE_GROWTH.get()) && isNextToSeal) return null;
        if (ctx.state().is(IsmBlocks.TRISTITIA_GRASS_GROWTH.get()) && !isNextToSeal) return null;

        return isNextToSeal
                ? new GrowingProposal(IsmBlocks.TRISTITIA_CORE_GROWTH.get(), 400)
                : new GrowingProposal(IsmBlocks.TRISTITIA_GRASS_GROWTH.get(), 50);
    }

    private @Nullable BlockPos checkDirection(GrowthContext ctx, Direction direction, Set<Direction> toCheck) {
        ServerLevel level = ctx.level();

        for (FlatDirection flat : FlatDirection.randomOrder(ctx.random())) {
            Direction checkDirection = flat.moveDirection(direction);

            scratchPos.setWithOffset(ctx.pos(), checkDirection);
            var state = level.getBlockState(scratchPos);
            if (state.getBlock() instanceof TristitiaClingyGrowthBlock) continue;

            if (!state.isSolidRender() && state.getFluidState().isEmpty()) {
                if (checkIfPlaceable(ctx, scratchPos, direction))
                    return scratchPos.immutable();
            }

            if (state.isSolidRender()) continue;

            scratchPos.move(direction);
            state = level.getBlockState(scratchPos);
            if (state.getBlock() instanceof TristitiaClingyGrowthBlock) continue;

            if (!state.isSolidRender() && state.getFluidState().isEmpty()) {
                if (checkIfPlaceable(ctx, scratchPos, checkDirection.getOpposite()))
                    return scratchPos.immutable();
            }
        }

        return null;
    }

    private boolean checkIfPlaceable(GrowthContext ctx, BlockPos pos, Direction face) {
        return MultifaceBlock.canAttachTo(ctx.level(), pos, face);
    }
}
