package com.ewoudje.ism.features.seal;

import com.ewoudje.ism.collections.IsmStructures;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import java.util.Optional;

public class SealStructure extends Structure {
    private static final int CHEES_REQ = 5;

    public static final MapCodec<SealStructure> CODEC = simpleCodec(SealStructure::new);

    protected SealStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        var chunkPos = context.chunkPos();

        if (!(context.chunkGenerator() instanceof NoiseBasedChunkGenerator noiseGenerator)) return Optional.empty();
        var sampler = context.randomState().getSampler(
                noiseGenerator.generatorSettings().value().noiseRouter().finalDensity()
        );

        int stepSize = 3;
        int y = context.heightAccessor().getMinY();
        int hits = 0;
        for (; y <= context.heightAccessor().getMaxY(); y += stepSize) {

            float value = sampler.sampleValue(
                    SamplerContext.EMPTY_UNCACHED,
                    chunkPos.getMiddleBlockX(),
                    y,
                    chunkPos.getMiddleBlockZ()
            );

            if (value > 0) {
                hits++;
            }
        }

        int totalSteps = context.heightAccessor().getHeight() / stepSize;

        if (hits < totalSteps / CHEES_REQ) {
            return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, builder -> this.generatePieces(builder, context));
        } else return Optional.empty();
    }

    private void generatePieces(StructurePiecesBuilder builder, Structure.GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();
        builder.addPiece(new SealStructurePiece(
                context.heightAccessor().getMinY(),
                context.heightAccessor().getMaxY(),
                chunkPos.getMinBlockX(),
                chunkPos.getMinBlockZ()
        ));
    }


    @Override
    public StructureType<?> type() {
        return IsmStructures.SEAL.get();
    }
}
