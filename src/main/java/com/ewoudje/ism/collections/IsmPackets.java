package com.ewoudje.ism.collections;

import com.ewoudje.ism.Ism;
import com.ewoudje.ism.features.notebook.AddNoteToNotebookPacket;
import com.ewoudje.ism.features.notebook.NotebookItem;
import com.ewoudje.ism.util.gui.menu.ClientMenuUtil;
import com.ewoudje.ism.util.gui.menu.ServerMenuUtil;
import com.ewoudje.ism.util.gui.menu.packets.CloseMenuC2SPacket;
import com.ewoudje.ism.util.gui.menu.packets.CloseMenuS2CPacket;
import com.ewoudje.ism.util.gui.menu.packets.MenuEventC2SPacket;
import com.ewoudje.ism.util.gui.menu.packets.MenuEventS2CPacket;
import com.ewoudje.ism.util.gui.menu.packets.OpenMenuPacket;
import com.ewoudje.ism.util.world.BiomeUtil;
import com.ewoudje.ism.util.world.SingleBiomeUpdatePacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;

@EventBusSubscriber
public class IsmPackets {
    public static final String PROTOCOL_VERSION = "1";

    public static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> type(String name) {
        return new CustomPacketPayload.Type<>(Ism.id(name));
    }

    @SubscribeEvent
    private static void registerPackets(RegisterPayloadHandlersEvent e) {
        var registrar = e.registrar(PROTOCOL_VERSION);
        var onMain = registrar.executesOn(HandlerThread.MAIN);
        onMain.playToClient(OpenMenuPacket.TYPE, OpenMenuPacket.STREAM_CODEC, ClientMenuUtil::openMenu);
        onMain.playToClient(CloseMenuS2CPacket.TYPE, CloseMenuS2CPacket.STREAM_CODEC, ClientMenuUtil::closeMenu);
        onMain.playToClient(MenuEventS2CPacket.TYPE, MenuEventS2CPacket.STREAM_CODEC, ClientMenuUtil::recvEvent);
        onMain.playToClient(SingleBiomeUpdatePacket.TYPE, SingleBiomeUpdatePacket.STREAM_CODEC, BiomeUtil::handlePacket);

        onMain.playToServer(CloseMenuC2SPacket.TYPE, CloseMenuC2SPacket.STREAM_CODEC, ServerMenuUtil::closeMenu);
        onMain.playToServer(AddNoteToNotebookPacket.TYPE, AddNoteToNotebookPacket.STREAM_CODEC, NotebookItem::addNoteToNotebook);
        onMain.playToServer(MenuEventC2SPacket.TYPE, MenuEventC2SPacket.STREAM_CODEC, ServerMenuUtil::recvEvent);
    }
}
