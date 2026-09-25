package com.ewoudje.ism.collections;

import com.ewoudje.ism.Ism;
import com.ewoudje.ism.features.note.Note;
import com.ewoudje.ism.features.notebook.NotebookData;
import com.ewoudje.ism.util.lore.Creator;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class IsmDataComponents {
    public static final DeferredRegister.DataComponents REGISTRY = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Ism.ID);

    public static final Supplier<DataComponentType<NotebookData>> NOTEBOOK = REGISTRY.registerComponentType(
            "notebook",
            b -> b
                    .persistent(NotebookData.CODEC)
                    .networkSynchronized(NotebookData.STREAM_CODEC)
    );

    public static final Supplier<DataComponentType<Creator>> CREATOR = REGISTRY.registerComponentType(
            "creator",
            b -> b
                    .persistent(Creator.CODEC)
                    .networkSynchronized(Creator.STREAM_CODEC)
    );

    public static final Supplier<DataComponentType<Note>> NOTE = REGISTRY.registerComponentType(
            "note",
            b -> b
                    .persistent(Note.CODEC)
                    .networkSynchronized(Note.STREAM_CODEC)
    );
}
