package com.ewoudje.ism.features.tristitia;

import com.ewoudje.ism.collections.IsmAttachments;
import com.ewoudje.ism.collections.IsmBiomes;
import com.ewoudje.ism.util.world.BiomeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import org.jspecify.annotations.Nullable;

public abstract class TristitiaGrowthBlock extends Block {
    private final double BIOME_CHANCE = 0.3;
    public TristitiaGrowthBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);

        level.getData(IsmAttachments.TRISTITIA_GROWTH_STATE).updateGrowth(pos, state);

        BlockPos targetPos = pos.relative(Direction.getRandom(random), 5);
        if (random.nextFloat() < BIOME_CHANCE && !BiomeUtil.getBiome(level, targetPos).is(IsmBiomes.TRISTITIA)) {
            BiomeUtil.setBiomeAround(level, targetPos, IsmBiomes.TRISTITIA);
        }
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        level.getData(IsmAttachments.TRISTITIA_GROWTH_STATE).updateGrowth(pos, state, true);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, block, orientation, movedByPiston);

        level.getData(IsmAttachments.TRISTITIA_GROWTH_STATE).updateGrowth(pos, state, true);
    }

    public BlockState getStateForGrowth(ServerLevel level, BlockPos pos, RandomSource random) {
        return defaultBlockState();
    }
}
