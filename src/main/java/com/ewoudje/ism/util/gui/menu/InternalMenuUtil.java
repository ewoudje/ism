package com.ewoudje.ism.util.gui.menu;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.connection.ConnectionType;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Map;

public abstract class InternalMenuUtil {
    private final Object2IntMap<CustomMenu> currentMenus = new Object2IntOpenHashMap<>();
    private final Logger logger;

    protected InternalMenuUtil(Logger logger) {
        currentMenus.defaultReturnValue(-1);
        this.logger = logger;
    }

    protected void addMenu(CustomMenu menu, int id) {
        currentMenus.put(menu, id);
    }

    protected void closeMenu(int id, boolean fromPacket) {
        CustomMenu menu = findMenu(id);
        if (menu == null) {
            logger.debug("Tried to close a menu with id {} it wasn't found, silently ignoring", id);
            return;
        }

        closeMenu(menu, id, fromPacket);
    }

    protected <T> void sendMenuEvent(CustomMenu menu, int i, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, T event) {
        int id = idOf(menu);
        if (id == -1) {
            logger.warn("Tried to send event with menu {} but it wasn't found", menu);
            return;
        }

        ByteBuf buf = Unpooled.buffer();
        buf.writeInt(id);
        buf.writeInt(i);
        codec.encode(
                new RegistryFriendlyByteBuf(buf, RegistryAccess.EMPTY, ConnectionType.NEOFORGE),
                event
        );

        sendEventPacket(buf, menu.player());
    }

    protected void recvEvent(ByteBuf data, IPayloadContext context) {
        CustomMenu menu = findMenu(data.readInt());
        int event = data.readInt();

        RegistryFriendlyByteBuf friendlyByteBuf =
                new RegistryFriendlyByteBuf(data, context.player().registryAccess(), ConnectionType.NEOFORGE);

        if (event == 1) {
            if (!(menu instanceof MenuEvent1 e1))
                throw new IllegalStateException("CustomMenu received event 1, but it does not support it?");

            e1.handleEvent1(e1.event1().decode(friendlyByteBuf));
        }
    }

    protected int idOf(CustomMenu menu) {
        return currentMenus.getInt(menu);
    }

    protected @Nullable CustomMenu findMenu(int id) {
        return currentMenus.object2IntEntrySet().stream()
                .filter(e -> e.getIntValue() == id)
                .findAny().map(Map.Entry::getKey).orElse(null);
    }


    protected abstract void sendEventPacket(ByteBuf data, Player player);
    protected abstract void sendClosePacket(int id, Player player);
    protected abstract void preClose(CustomMenu menu);

    protected void closeMenu(CustomMenu menu, int id, boolean fromPacket) {
        if (id == -1) {
            logger.debug("Tried to close menu {} it wasn't found, silently ignoring", menu);
            return;
        }

        if (menu.isClosed())
            throw new IllegalStateException("Found menu with id " + id + " but already closed?");

        if (id != currentMenus.removeInt(menu))
            throw new IllegalStateException("How did this happen, call help");

        logger.debug("Closing menu {} with id {}", menu, id);
        preClose(menu);
        menu.onClose();

        if (!fromPacket)
            sendClosePacket(id, menu.player());
    }
}
