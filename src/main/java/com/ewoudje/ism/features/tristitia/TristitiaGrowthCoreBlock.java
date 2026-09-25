package com.ewoudje.ism.features.tristitia;

import com.ewoudje.ism.collections.IsmBlockEntities;
import com.ewoudje.ism.util.block.BlockWithEntity;
import com.ewoudje.ism.util.server.ServerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class TristitiaGrowthCoreBlock extends TristitiaGrowthBlock implements BlockWithEntity<TristitiaGrowthCoreBlockEntity> {
    public TristitiaGrowthCoreBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new TristitiaGrowthCoreBlockEntity(worldPosition, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> actualType) {
        if (level.isClientSide()) return null;

        return BlockWithEntity.createTickerHelper(
                actualType,
                IsmBlockEntities.TRISTITIA_CORE,
                (innerLevel, pos, state, entity) ->
                        TristitiaGrowthCoreBlockEntity.serverTick(ServerUtil.getLevel(innerLevel), pos, state, entity)
        );
    }
}
