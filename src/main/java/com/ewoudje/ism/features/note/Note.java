package com.ewoudje.ism.features.note;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public sealed interface NoteData {
    Codec<NoteData> CODEC = Codec.STRING.fieldOf("type").dispatch(
            data -> "overlay_texture",
            type -> switch (type) {
                case "overlay_texture" -> Identifier.CODEC
                        .xmap(OverlayTexture::new, OverlayTexture::texture)
                        .fieldOf("texture");

                default -> throw new IllegalStateException("Invalid type for NoteData: " + type);
            });

    StreamCodec<ByteBuf, NoteData> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    record OverlayTexture(Identifier texture) implements NoteData {

    }
}
