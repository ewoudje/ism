package com.ewoudje.ism.util.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public interface BlockWithEntity<T extends BlockEntity> extends EntityBlock {

    default T getBlockEntity(Level level, BlockPos pos) {
        return (T) level.getBlockEntity(pos);
    }

    static <E extends BlockEntity, A extends BlockEntity> @Nullable BlockEntityTicker<A> createTickerHelper(
            BlockEntityType<A> actual, Supplier<BlockEntityType<E>> expected, @Nullable BlockEntityTicker<? super E> ticker
    ) {
        return expected.get() == actual ? (BlockEntityTicker<A>)ticker : null;
    }
}
