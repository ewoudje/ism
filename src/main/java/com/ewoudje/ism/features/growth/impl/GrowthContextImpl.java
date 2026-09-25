package com.ewoudje.ism.features.growth.impl;

import com.ewoudje.ism.features.growth.GrowingProposal;
import com.ewoudje.ism.features.growth.GrowthCapability;
import com.ewoudje.ism.features.growth.GrowthContext;
import com.ewoudje.ism.features.tristitia.growth.TristitiaGrowthSample;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jspecify.annotations.Nullable;

public class GrowthContextImpl implements GrowthContext {
    private final ServerLevel level;
    private final BlockPos pos;
    private final BlockState state;
    private final GrowthCapability capability;
    private boolean gotNextTickSpeed, gotGrowingProposal, gotSelfGrowingProposal, gotSample, gotGrowthTarget;
    private @Nullable TristitiaGrowthSample sample;
    private @Nullable BlockPos growTarget;
    private @Nullable GrowingProposal growingProposal, selfGrowthProposal;
    private int nextTickSpeed;

    public GrowthContextImpl(ServerLevel level, BlockPos pos, BlockState state, GrowthCapability capability) {
        this.level = level;
        this.pos = pos;
        this.state = state;
        this.capability = capability;
    }


    @Override
    public ServerLevel level() {
        return level;
    }

    @Override
    public BlockPos pos() {
        return pos;
    }

    @Override
    public BlockState state() {
        return state;
    }

    @Override
    public RandomSource random() {
        return level.getRandom();
    }

    @Override
    public @Nullable TristitiaGrowthSample sample() {
        if (!gotSample) {
            gotSample = true;
            sample = TristitiaGrowthSample.sample(level, pos);
        }

        return sample;
    }

    @Override
    public int nextTickSpeed() {
        if (!gotNextTickSpeed) {
            gotNextTickSpeed = true;
            nextTickSpeed = capability.nextTrySpeed(this);
        }

        return nextTickSpeed;
    }

    @Override
    public @Nullable GrowingProposal proposal() {
        if (!gotGrowingProposal) {
            gotGrowingProposal = true;
            growingProposal = capability.createGrowProposal(this);
        }

        return growingProposal;
    }

    @Override
    public @Nullable GrowingProposal selfProposal() {
        if (!gotSelfGrowingProposal) {
            gotSelfGrowingProposal = true;
            selfGrowthProposal = capability.selfGrowthProposal(this);
        }

        return selfGrowthProposal;
    }

    @Override
    public @Nullable BlockPos growthTarget() {
        if (!gotGrowthTarget) {
            gotGrowthTarget = true;
            growTarget = capability.requestGrow(this);
        }

        return growTarget;
    }

    @Override
    public @Nullable BlockEntity getBlockEntity(BlockPos pos) {
        return level.getBlockEntity(pos);
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        if (pos.equals(this.pos)) return state;
        return level.getBlockState(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return level.getFluidState(pos);
    }

    @Override
    public int getHeight() {
        return level.getHeight();
    }

    @Override
    public int getMinY() {
        return level.getMinY();
    }
}
