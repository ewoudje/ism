package com.ewoudje.ism.features.tristitia;

import com.ewoudje.ism.collections.IsmBlockEntities;
import com.ewoudje.ism.collections.IsmCapabilities;
import com.ewoudje.ism.util.block.IsmBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class TristitiaGrowthCoreBlockEntity extends IsmBlockEntity {
    private int energy;

    public TristitiaGrowthCoreBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(IsmBlockEntities.TRISTITIA_CORE.get(), worldPosition, blockState);
    }

    public static void serverTick(ServerLevel serverLevel, BlockPos pos, BlockState state, TristitiaGrowthCoreBlockEntity self) {
        self.energy += 5;
    }

    @Override
    public void onLoad() {
        super.onLoad();

        trackCapability(IsmCapabilities.TRISTITIA_POI);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        energy = input.getIntOr("energy", 0);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("energy", energy);
    }

    public float aggressiveness() {
        return 0;
    }

    public int availableEnergy() {
        return energy;
    }

    public boolean consumeEnergy(int requested) {
        if (requested > energy) return false;
        energy -= requested;
        return true;
    }
}
