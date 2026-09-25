package com.ewoudje.ism.features.tristitia.growth;

import com.ewoudje.ism.collections.IsmAttachments;
import com.ewoudje.ism.collections.IsmCapabilities;
import com.ewoudje.ism.features.growth.GrowingProposal;
import com.ewoudje.ism.features.growth.GrowthContext;
import com.ewoudje.ism.features.growth.impl.GrowthContextImpl;
import com.ewoudje.ism.features.tristitia.poi.TristitiaPOI;
import com.ewoudje.ism.util.block.BlockEntityCapabilityResult;
import com.ewoudje.ism.util.block.BlockEntityTrackedCapabilities;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.joml.Vector3dc;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.OptionalLong;
import java.util.Set;
import java.util.stream.Stream;

@EventBusSubscriber
public class TristitiaGrowthWorldState {
    private final ServerLevel level;
    private final Set<TristitiaPOI> pois = new HashSet<>();
    private final BlockEntityTrackedCapabilities<TristitiaPOI, Void>.LevelTracked bePois;
    private final Long2ObjectMap<LongSet> toBeTicked = new Long2ObjectOpenHashMap<>();

    public TristitiaGrowthWorldState(IAttachmentHolder holder) {
        if (!(holder instanceof ServerLevel level))
            throw new IllegalArgumentException();

        this.level = level;
        this.bePois = BlockEntityTrackedCapabilities.get(IsmCapabilities.TRISTITIA_POI, level);
    }

    public @Nullable TristitiaGrowthSample sample(Vector3dc pos) {
        var result = Stream.concat(
                        pois.stream(),
                        bePois.stream(null)
                                .map(BlockEntityCapabilityResult::capability)
                )
                .filter(p -> !Double.isNaN(p.reach()))
                .reduce(
                        new TristitiaGrowthSample(0, null),

                        (sample, poi) -> {
                            double reach = poi.reach() - poi.center().distance(pos);
                            return sample.density() < reach
                                    ? new TristitiaGrowthSample(reach, poi.getRoot())
                                    : sample;
                        },

                        (sample1, sample2) ->
                                sample1.density() < sample2.density() ? sample2 : sample1
                );

        if (result.density() <= 0) return null;
        return result;
    }

    public void updateGrowth(BlockPos pos, BlockState state) {
        updateGrowth(pos, state, false);
    }

    public void updateGrowth(BlockPos pos, BlockState state, boolean noGrowth) {
        if (!level.isLoaded(pos)) return; // Chunks are staying loaded somehow...
        var capability = level.getCapability(IsmCapabilities.GROWTH, pos);
        if (capability == null) return;

        var context = new GrowthContextImpl(
                level,
                pos,
                state,
                capability
        );

        addFutureTick(context);
        if (noGrowth) return;

        if (!handleProposal(context))
            handleSelfGrowth(context);
    }

    private boolean handleProposal(GrowthContext context) {
        var sample = context.sample();
        if (sample == null) return false;

        BlockPos target = context.growthTarget();
        if (target == null) return false;

        GrowingProposal proposal = context.proposal();
        if (proposal == null) return false;
        if (!sample.closest().consumeEnergy(proposal.energyConsumption())) return false;

        BlockState proposedState = proposal.block().getStateForGrowth(level, target, context.random());
        level.setBlock(target, proposedState, Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS);
        updateGrowth(target, proposedState, true);
        return true;
    }

    private void handleSelfGrowth(GrowthContext context) {
        GrowingProposal proposal = context.selfProposal();
        if (proposal == null) return;

        BlockState proposedState = proposal.block().getStateForGrowth(level, context.pos(), context.random());
        level.setBlock(context.pos(), proposedState, Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS);
    }

    private int scramble = 0;
    private void addFutureTick(GrowthContext context) {
        var time = context.nextTickSpeed();
        if (time <= 0) return;
        time += scramble++ % 4; // Scrambles the timings so its more spread out

        var lst = toBeTicked.computeIfAbsent(level.getGameTime() + time, l -> new LongArraySet());
        lst.add(context.pos().asLong());
    }

    @SubscribeEvent
    private static void tickServerLevel(LevelTickEvent.Post event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        TristitiaGrowthWorldState self = level.getData(IsmAttachments.TRISTITIA_GROWTH_STATE);
        LongSet toTick = self.toBeTicked.remove(level.getGameTime());
        if (toTick == null || toTick.isEmpty()) return;


        // Current problem with this solution is that you can the same block multiple times ticked, as long as its spread out.
        // For example, it gets ticked on tick 10, and should be reticked after 50 ticks.
        // If we then tick it because of other reasons at tick 20 and again for 50 ticks.
        // It will get ticked on both tick 60 and 70 even tough the spacing should be 50
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (LongIterator iter = toTick.iterator(); iter.hasNext(); ) {
            pos.set(iter.nextLong());
            self.updateGrowth(pos, level.getBlockState(pos));
        }

        if (level.getGameTime() % 100 == 0) {
            OptionalLong min = self.toBeTicked.keySet().longStream().min();
            if (min.isPresent()) {
                if (min.getAsLong() < level.getGameTime())
                    throw new RuntimeException();
            }
        }
    }
}
