package com.ewoudje.ism.features.notebook;

import com.ewoudje.ism.collections.IsmPackets;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record AddNoteToNotebookPacket(int noteSlot, int notebookSlot) implements CustomPacketPayload {
    public static final Type<AddNoteToNotebookPacket> TYPE = IsmPackets.type("add_note_to_notebook");
    public static final StreamCodec<ByteBuf, AddNoteToNotebookPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, AddNoteToNotebookPacket::noteSlot,
            ByteBufCodecs.VAR_INT, AddNoteToNotebookPacket::notebookSlot,
            AddNoteToNotebookPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
