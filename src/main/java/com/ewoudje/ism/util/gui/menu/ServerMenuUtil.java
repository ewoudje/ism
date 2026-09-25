package com.ewoudje.ism.util.gui.menu;

import com.ewoudje.ism.util.gui.menu.packets.CloseMenuC2SPacket;
import com.ewoudje.ism.util.gui.menu.packets.CloseMenuS2CPacket;
import com.ewoudje.ism.util.gui.menu.packets.MenuEventC2SPacket;
import com.ewoudje.ism.util.gui.menu.packets.MenuEventS2CPacket;
import com.ewoudje.ism.util.gui.menu.packets.OpenMenuPacket;
import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.TagValueOutput;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.slf4j.Logger;

public class ServerMenuUtil extends InternalMenuUtil {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ServerMenuUtil INSTANCE = new ServerMenuUtil(LOGGER);
    private static int counter = 0;

    protected ServerMenuUtil(Logger logger) {
        super(logger);
    }

    public static void closeMenu(CustomMenu menu) {
        if (!(menu.player() instanceof ServerPlayer player))
            throw new IllegalStateException("Cannot close menu via ServerMenuUtil when the menu doesnt contain a server player");

        INSTANCE.closeMenu(menu, INSTANCE.idOf(menu), false);
    }

    public static void closeMenu(CloseMenuC2SPacket p, IPayloadContext context) {
        INSTANCE.closeMenu(p.id(), true);
    }

    public static void openMenu(CustomMenu menu) {
        if (menu.isClosed())
            throw new IllegalStateException("Menu is already closed?");

        int id = INSTANCE.idOf(menu);
        if (id != -1)
            throw new IllegalStateException("Menu is already open?");

        if (!(menu.player() instanceof ServerPlayer player))
            throw new IllegalStateException("Cannot open menu via ServerMenuUtil when the menu doesnt contain a server player");

        LOGGER.debug("Opening menu {}", menu);
        id = counter++;

        INSTANCE.addMenu(menu, id);
        try (var reporter = new ProblemReporter.ScopedCollector(LOGGER)) {
            var output = TagValueOutput.createWithContext(reporter, menu.player().registryAccess());
            menu.toClientData(output);
            PacketDistributor.sendToPlayer(player, new OpenMenuPacket(menu.type(), id, output.buildResult()));
        }
    }

    public static <T> void sendEvent(CustomMenu menu, int i, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, T event) {
        INSTANCE.sendMenuEvent(menu, i, codec, event);
    }


    public static void recvEvent(MenuEventC2SPacket p, IPayloadContext context) {
        INSTANCE.recvEvent(p.data(), context);
    }

    @Override
    protected void sendEventPacket(ByteBuf data, Player player) {
        PacketDistributor.sendToPlayer((ServerPlayer) player, new MenuEventS2CPacket(data));
    }

    @Override
    protected void sendClosePacket(int id, Player player) {
        PacketDistributor.sendToPlayer((ServerPlayer) player, new CloseMenuS2CPacket(id));
    }

    @Override
    protected void preClose(CustomMenu menu) {

    }
}
