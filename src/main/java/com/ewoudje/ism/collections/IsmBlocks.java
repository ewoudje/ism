package com.ewoudje.ism.collections;

import com.ewoudje.ism.Ism;
import com.ewoudje.ism.features.alchemy.AlchemyTableBlock;
import com.ewoudje.ism.features.seal.SealFillingBlock;
import com.ewoudje.ism.features.seal.SealStoneBlock;
import com.ewoudje.ism.features.tristitia.TristitiaClingyGrowthBlock;
import com.ewoudje.ism.features.tristitia.TristitiaGrowthCoreBlock;
import com.ewoudje.ism.features.tristitia.TristitiaGrowthHoleBlock;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@EventBusSubscriber
public class IsmBlocks {
    public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(Ism.ID);

    public static final Supplier<SealStoneBlock> SEAL_STONE =
            registerBlock("sealstone", SealStoneBlock::new);
    public static final Supplier<SealFillingBlock> SEAL_FILLING =
            registerBlock("seal_filling", SealFillingBlock::new);
    public static final Supplier<AlchemyTableBlock> ALCHEMY_TABLE =
            registerBlock("alchemy_table", AlchemyTableBlock::new);

    public static final Supplier<TristitiaClingyGrowthBlock> TRISTITIA_VINE_GROWTH =
            registerBlock("tristitia_vine_growth", p -> new TristitiaClingyGrowthBlock(p, 0.1f), p -> p
                    .mapColor(MapColor.COLOR_MAGENTA)
                    .noCollision()
                    .randomTicks()
                    .strength(0.2F)
                    .sound(SoundType.VINE)
                    .pushReaction(PushReaction.POPPED)
            );

    public static final Supplier<TristitiaClingyGrowthBlock> TRISTITIA_GRASS_GROWTH =
            registerBlock("tristitia_grass_growth", p -> new TristitiaClingyGrowthBlock(p, 1f), p -> p
                    .mapColor(MapColor.COLOR_MAGENTA)
                    .randomTicks()
                    .strength(0.2F)
                    .sound(SoundType.GRASS)
                    .pushReaction(PushReaction.POPPED)
            );

    public static final Supplier<TristitiaClingyGrowthBlock> TRISTITIA_CORE_GROWTH =
            registerBlock("tristitia_core_growth", p -> new TristitiaClingyGrowthBlock(p, 4f), p -> p
                    .mapColor(MapColor.COLOR_MAGENTA)
                    .randomTicks()
                    .strength(-1.0F, 3600000.0F)
                    .sound(SoundType.FUNGUS)
                    .pushReaction(PushReaction.IMMOVEABLE)
            );

    public static final Supplier<TristitiaGrowthCoreBlock> TRISTITIA_CORE =
            registerBlock("tristitia_core", TristitiaGrowthCoreBlock::new, p -> p
                    .randomTicks()
                    .pushReaction(PushReaction.IMMOVEABLE)
                    .strength(-1.0F, 3600000.0F)
                    .sound(SoundType.FUNGUS)
                    .noLootTable()
                    .isValidSpawn(Blocks::never)
            );

    public static final Supplier<TristitiaGrowthHoleBlock> TRISTITIA_SEAL_HOLE =
            registerBlock("tristitia_seal_hole", TristitiaGrowthHoleBlock::new, p -> p
                    .randomTicks()
                    .pushReaction(PushReaction.IMMOVEABLE)
                    .strength(-1.0F, 3600000.0F)
                    .sound(SoundType.FUNGUS)
                    .noLootTable()
                    .isValidSpawn(Blocks::never)
            );



    private static <T extends Block> Supplier<T> registerBlock(String name, Function<Block.Properties, T> function) {
        return registerBlock(name, function, UnaryOperator.identity());
    }

    private static <T extends Block> Supplier<T> registerBlock(String name, Function<Block.Properties, T> function, UnaryOperator<Block.Properties> properties) {
        return REGISTRY.registerBlock(name, function, properties);
    }

    @SubscribeEvent
    private static void configureTinting(RegisterColorHandlersEvent.BlockTintSources event) {
        event.register(List.of(BlockTintSources.foliage()), TRISTITIA_VINE_GROWTH.get());
    }
}
