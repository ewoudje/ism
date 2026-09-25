package com.ewoudje.ism.features.notebook;

import com.ewoudje.ism.collections.IsmCreators;
import com.ewoudje.ism.features.note.Note;
import com.ewoudje.ism.util.lore.Creator;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Random;

public record NotebookData(List<NoteEntry> collectedNotes) {
    public static final Codec<NotebookData> CODEC = RecordCodecBuilder.create(i ->
            i.group(
                    NoteEntry.CODEC
                            .listOf()
                            .fieldOf("collectedNotes")
                            .forGetter(NotebookData::collectedNotes)
            ).apply(i, NotebookData::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, NotebookData> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC);
    public NotebookData() {
        this(List.of());
    }

    public NotebookData addNote(@Nullable Creator creator, Note note) {
        var list = ImmutableList.<NoteEntry>builder();
        var random = new Random();
        list.add(new NoteEntry(
                creator == null ? IsmCreators.UNKNOWN : creator,
                note,
                collectedNotes.stream().mapToInt(NoteEntry::height).max().orElse(0) + 1,
                random.nextFloat(10),
                random.nextFloat(10),
                (float) (((random.nextFloat(20) - 10f) / 180f) * Math.PI)
        ));
        list.addAll(collectedNotes);
        return new NotebookData(list.build());
    }

    public record NoteEntry(Creator creator, Note note, int height, float x, float y, float rot) {
        public static final Codec<NoteEntry> CODEC = RecordCodecBuilder.create(i ->
                i.group(
                        Creator.CODEC.fieldOf("creator").forGetter(NoteEntry::creator),
                        Note.CODEC.fieldOf("note").forGetter(NoteEntry::note),
                        Codec.INT.fieldOf("height").forGetter(NoteEntry::height),
                        Codec.FLOAT.fieldOf("x").forGetter(NoteEntry::x),
                        Codec.FLOAT.fieldOf("y").forGetter(NoteEntry::y),
                        Codec.FLOAT.fieldOf("rot").forGetter(NoteEntry::rot)
                ).apply(i, NoteEntry::new)
        );
    }
}
