package com.ewoudje.ism.collections;

import com.ewoudje.ism.Ism;
import com.ewoudje.ism.features.alchemy.AlchemyTableBlockEntity;
import com.ewoudje.ism.features.tristitia.TristitiaGrowthCoreBlockEntity;
import com.ewoudje.ism.features.tristitia.TristitiaGrowthHoleBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class IsmBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Ism.ID);

    public static final Supplier<BlockEntityType<TristitiaGrowthCoreBlockEntity>> TRISTITIA_CORE =
            REGISTRY.register("tristitia_core", () -> new BlockEntityType<>(
                            TristitiaGrowthCoreBlockEntity::new,
                            IsmBlocks.TRISTITIA_CORE.get()
                    )
            );

    public static final Supplier<BlockEntityType<TristitiaGrowthHoleBlockEntity>> TRISTITIA_SEAL_HOLE =
            REGISTRY.register("tristitia_seal_hole", () -> new BlockEntityType<>(
                            TristitiaGrowthHoleBlockEntity::new,
                            IsmBlocks.TRISTITIA_SEAL_HOLE.get()
                    )
            );

    public static final Supplier<BlockEntityType<AlchemyTableBlockEntity>> ALCHEMY_TABLE =
            REGISTRY.register("alchemy_table", () -> new BlockEntityType<>(
                            AlchemyTableBlockEntity::new,
                            IsmBlocks.ALCHEMY_TABLE.get()
                    )
            );

}
