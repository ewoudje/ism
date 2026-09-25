package com.ewoudje.ism.features.seal;

import com.ewoudje.ism.collections.IsmBlocks;
import com.ewoudje.ism.collections.IsmStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

public class SealStructurePiece extends StructurePiece {
    public static final int CORE_REL_X = 8;
    public static final int CORE_Y = 0;
    public static final int CORE_REL_Z = 8;

    private final BlockPos.MutableBlockPos scratchPos = new BlockPos.MutableBlockPos();

    public SealStructurePiece(int minY, int maxY, int x, int z) {
        super(
                IsmStructures.SEAL_PIECE.get(),
                0,
                StructurePiece.makeBoundingBox(
                        x, minY, z,
                        Direction.NORTH,
                        16, maxY - minY, 16
                )
        );
        setOrientation(Direction.NORTH);
    }

    public SealStructurePiece(StructurePieceSerializationContext ctx, CompoundTag tag) {
        super(IsmStructures.SEAL_PIECE.get(), tag);
    }



    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {

    }

    @Override
    public void postProcess(
            WorldGenLevel level,
            StructureManager structureManager,
            ChunkGenerator generator,
            RandomSource random,
            BoundingBox chunkBB,
            ChunkPos chunkPos,
            BlockPos referencePos
    ) {
        int offset = random.nextInt(5) - 2;

        int sealHeight = level.getHeight(
                Heightmap.Types.WORLD_SURFACE_WG,
                getWorldX(CORE_REL_X, CORE_REL_Z),
                getWorldZ(CORE_REL_X, CORE_REL_Z)
        ) + offset - level.getMinY();


        placePillar(level, sealHeight, CORE_REL_X - 4, CORE_REL_Z - 4,chunkBB);
        placePillar(level, sealHeight, CORE_REL_X - 4, CORE_REL_Z + 4, chunkBB);
        placePillar(level, sealHeight, CORE_REL_X + 4, CORE_REL_Z + 4, chunkBB);
        placePillar(level, sealHeight, CORE_REL_X + 4, CORE_REL_Z - 4, chunkBB);

        int lX = CORE_REL_X - 2;
        int lZ = CORE_REL_Z - 2;
        int hX = CORE_REL_X + 2;
        int hZ = CORE_REL_Z + 2;

        for (int y = 0; y < sealHeight; y++) {
            for (int x = lX; x <= hX; x++) {
                for (int z = lZ; z <= hZ; z++) {
                    if (x == lX || z == lZ || x == hX || z == hZ) {
                        this.placeBlock(level, IsmBlocks.SEAL_STONE.get().defaultBlockState(), x, y, z, chunkBB);
                    } else {
                        this.placeBlock(level, IsmBlocks.SEAL_FILLING.get().defaultBlockState(), x, y, z, chunkBB);
                    }
                }
            }
        }

        for (int x = lX; x <= hX; x++) {
            for (int z = lZ; z <= hZ; z++) {
                this.placeBlock(level, IsmBlocks.SEAL_STONE.get().defaultBlockState(), x, sealHeight, z, chunkBB);
            }
        }

        this.placeBlock(
                level,
                IsmBlocks.TRISTITIA_CORE.get().defaultBlockState(),
                CORE_REL_X,
                CORE_Y - boundingBox.minY(),
                CORE_REL_Z,
                chunkBB
        );
    }

    public void placePillar(WorldGenLevel level, int height, int x, int z, BoundingBox chunkBB) {
        scratchPos.set(getWorldX(x, z), height, getWorldZ(x, z));
        for (int y = height + 10; y >= level.getMinY(); y--) {
            scratchPos.setY(y);
            if (chunkBB.isInside(scratchPos)) {
                if (level.getBlockState(scratchPos).is(BlockTags.BASE_STONE_OVERWORLD)) return;
                this.placeBlock(level, IsmBlocks.SEAL_STONE.get().defaultBlockState(), x, y, z, chunkBB);
            }
        }
    }
}
