package com.ewoudje.ism.collections;

import com.ewoudje.ism.Ism;
import com.ewoudje.ism.features.seal.SealStructure;
import com.ewoudje.ism.features.seal.SealStructurePiece;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class IsmStructures {
    public static final DeferredRegister<StructureType<?>> REGISTRY =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, Ism.ID);

    public static final DeferredRegister<StructurePieceType> PIECES_REGISTRY =
            DeferredRegister.create(Registries.STRUCTURE_PIECE, Ism.ID);

    public static final Supplier<StructureType<SealStructure>> SEAL =
            REGISTRY.register("seal", () -> () -> SealStructure.CODEC);

    public static final Supplier<StructurePieceType> SEAL_PIECE =
            PIECES_REGISTRY.register("seal", () -> SealStructurePiece::new);
}
