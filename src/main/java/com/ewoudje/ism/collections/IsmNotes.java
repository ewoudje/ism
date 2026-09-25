package com.ewoudje.ism.collections;

import com.ewoudje.ism.Ism;
import com.ewoudje.ism.features.note.Note;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class IsmNotes {
    public static final DeferredRegister<Note> REGISTRY = DeferredRegister.create(Note.KEY, Ism.ID);

    public static final Supplier<Note> UNSEAL = REGISTRY.register("unseal",
            () -> new Note.WithBackground(Ism.id("unseal_text")));
    public static final Supplier<Note> TRISTITIA_GROWTH = REGISTRY.register("tristitia_growth",
            () -> new Note.NoBackground(Ism.id("tristitia_growth")));

    public static final Supplier<Note> TRISTITIA_VINE_GROWTH = REGISTRY.register("tristitia_vine_growth",
            () -> new Note.NoBackground(Ism.id("tristitia_vine_growth")));
}
