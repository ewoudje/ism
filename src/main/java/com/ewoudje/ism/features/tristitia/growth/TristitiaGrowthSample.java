package com.ewoudje.ism.features.tristitia.growth;

import com.ewoudje.ism.collections.IsmAttachments;
import com.ewoudje.ism.features.tristitia.poi.TristitiaPOI;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.fml.common.EventBusSubscriber;
import org.joml.Vector3d;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@EventBusSubscriber
public record TristitiaGrowthSample(double density, TristitiaPOI closest) {
    public static @Nullable TristitiaGrowthSample sample(ServerLevel level, BlockPos pos) {
        return level.getData(IsmAttachments.TRISTITIA_GROWTH_STATE).sample(
                new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)
        );
    }

    /*
    @SubscribeEvent
    private static void registerDebugEntry(RegisterDebugEntriesEvent event) {
        event.register(Ism.id("tristitia_growth"), (displayer, serverOrClientLevel, clientChunk, serverChunk) -> {
            if (serverOrClientLevel instanceof ServerLevel serverLevel) {
                Vec3 pos = Minecraft.getInstance().player.position();
                TristitiaGrowthSample sample = serverLevel
                        .getData(IsmAttachments.TRISTITIA_GROWTH_STATE)
                        .sample(new Vector3d(pos.x, pos.y, pos.z));

                if (sample != null) {
                    displayer.addLine("Tristitia Sample: " + sample);
                }
            }
        });
    }*/ // TODO capabilities dont play nice

    @Override
    public @NonNull String toString() {
        return String.format("D: %.2f, closest: %s", density, closest);
    }
}
