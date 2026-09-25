package com.ewoudje.ism.util.gui.menu;

import com.ewoudje.ism.util.gui.menu.packets.CloseMenuC2SPacket;
import com.ewoudje.ism.util.gui.menu.packets.CloseMenuS2CPacket;
import com.ewoudje.ism.util.gui.menu.packets.MenuEventC2SPacket;
import com.ewoudje.ism.util.gui.menu.packets.MenuEventS2CPacket;
import com.ewoudje.ism.util.gui.menu.packets.OpenMenuPacket;
import com.ewoudje.ism.util.gui.screen.CustomScreen;
import com.mojang.logging.LogUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.TagValueInput;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.slf4j.Logger;

public class ClientMenuUtil extends InternalMenuUtil {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ClientMenuUtil INSTANCE = new ClientMenuUtil(LOGGER);

    private ClientMenuUtil(Logger logger) {
        super(logger);
    }

    public static void openMenu(OpenMenuPacket p, IPayloadContext context) {
        var player = Minecraft.getInstance().player;
        if (player == null)
            throw new IllegalStateException("Player is null?");

        try (var reporter = new ProblemReporter.ScopedCollector(LOGGER)) {
            var input = TagValueInput.create(reporter, player.registryAccess(), p.data());
            var menu = p.menuType().initMenu(player, input);
            var screen = ((CustomMenuType) p.menuType()).initScreen(menu);
            if (!(screen instanceof Screen mcScreen))
                throw new IllegalStateException("CustomScreen is not a Screen!");

            var gui = Minecraft.getInstance().gui;
            LOGGER.debug("Opening menu {} with id {}", menu, p.id());

            INSTANCE.addMenu(menu, p.id());
            gui.setScreen(mcScreen);
        }
    }

    public static void closeMenu(CustomMenu menu) {
        INSTANCE.closeMenu(menu, INSTANCE.idOf(menu), false);
    }

    public static void closeMenu(CloseMenuS2CPacket p, IPayloadContext context) {
        INSTANCE.closeMenu(p.id(), true);
    }

    public static void recvEvent(MenuEventS2CPacket p, IPayloadContext context) {
        INSTANCE.recvEvent(p.data(), context);
    }

    public static <T> void sendEvent(CustomMenu menu, int i, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, T event) {
        INSTANCE.sendMenuEvent(menu, i, codec, event);
    }

    @Override
    protected void sendEventPacket(ByteBuf data, Player player) {
        ClientPacketDistributor.sendToServer(new MenuEventC2SPacket(data));
    }

    @Override
    protected void sendClosePacket(int id, Player player) {
        ClientPacketDistributor.sendToServer(new CloseMenuC2SPacket(id));
    }

    @Override
    protected void preClose(CustomMenu menu) {
        var gui = Minecraft.getInstance().gui;
        if (!(gui.screen() instanceof CustomScreen<?> cs))
            return;

        if (cs.menu() != menu)
            return;

        Minecraft.getInstance().gui.setScreen(null);
    }
}
