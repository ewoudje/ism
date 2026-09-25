package com.ewoudje.ism.util.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class BiomeUtil {

    public static void setBiomeAround(Level level, BlockPos pos, Identifier biome) {
        setBiomeAround(level, pos,
                level.registryAccess()
                        .lookupOrThrow(Registries.BIOME)
                        .get(biome).orElseThrow()
        );
    }

    public static void setBiomeAround(Level level, BlockPos pos, Holder<Biome> biome) {
        setBiome(level, pos.getX() >> 2, pos.getY() >> 2,  pos.getZ() >> 2, biome);
    }

    public static void setBiome(Level level, int quartX, int quartY, int quartZ, Holder<Biome> biome) {
        int sX = quartX >> 2;
        int sY = quartY >> 2;
        int sZ = quartZ >> 2;

        ChunkAccess chunk = level.getChunk(sX, sZ);
        if (level instanceof ServerLevel sl)
            PacketDistributor.sendToPlayersTrackingChunk(sl, chunk.getPos(),
                    new SingleBiomeUpdatePacket(BlockPos.asLong(quartX, quartY, quartZ), biome));

        LevelChunkSection section = chunk.getSection(chunk.getSectionIndexFromSectionY(sY));

        // Its read-only, not sure why... could be cached somewhere? sounds odd
        PalettedContainer<Holder<Biome>> biomes = (PalettedContainer<Holder<Biome>>) section.getBiomes();
        biomes.set(quartX & 3, quartY & 3, quartZ & 3, biome);

        if (level.isClientSide())
            ClientBiomeUtil.markBiomeSectionDirty(sX, sY, sZ);

        chunk.markUnsaved();
    }

    public static void handlePacket(SingleBiomeUpdatePacket packet, IPayloadContext ctx) {
        Vec3i quart = BlockPos.of(packet.packedQuart());
        Level level = ctx.player().level();

        setBiome(level, quart.getX(), quart.getY(), quart.getZ(), packet.biome());
    }

    public static Holder<Biome> getBiome(Level level, BlockPos pos) {
        return level.getNoiseBiome(pos.getX() >> 2, pos.getY() >> 2, pos.getZ() >> 2);
    }
}
