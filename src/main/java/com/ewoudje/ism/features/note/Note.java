package com.ewoudje.ism.features.note;

import com.ewoudje.ism.Ism;
import com.ewoudje.ism.collections.IsmRegistries;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public interface Note {
    ResourceKey<Registry<Note>> KEY = ResourceKey.createRegistryKey(Ism.id("note"));
    Codec<Note> CODEC = IsmRegistries.NOTES.byNameCodec();
    StreamCodec<RegistryFriendlyByteBuf, Note> STREAM_CODEC = ByteBufCodecs.registry(KEY);

    NoteRenderer<? extends Note> renderer();

    interface SimpleTexture extends Note {
        Identifier texture();
    }

    record WithBackground(Identifier texture) implements SimpleTexture {
        @Override
        public NoteRenderer<SimpleTexture> renderer() {
            return NoteRenderer.SimpleTextureRenderer.WITH_BACKGROUND;
        }
    }

    record NoBackground(Identifier texture) implements SimpleTexture {
        @Override
        public NoteRenderer<SimpleTexture> renderer() {
            return NoteRenderer.SimpleTextureRenderer.WITHOUT_BACKGROUND;
        }
    }
}
