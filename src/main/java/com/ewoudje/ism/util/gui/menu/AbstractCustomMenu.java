package com.ewoudje.ism.util.gui.menu;

import net.minecraft.world.entity.player.Player;

public abstract class AbstractCustomMenu implements CustomMenu {
    protected final Player player;
    private boolean closed = false;

    protected AbstractCustomMenu(Player player) {
        this.player = player;
    }

    protected boolean isClientSide() {
        return player.level().isClientSide();
    }

    protected void close() {
        if (closed) throw new IllegalStateException("Already closed");

        if (isClientSide()) {
            ClientMenuUtil.closeMenu(this);
        } else {
            ServerMenuUtil.closeMenu(this);
        }

        if (!closed) throw new IllegalStateException("Menu was not properly closed");
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void onClose() {
        closed = true;
    }

    @Override
    public Player player() {
        return player;
    }

    @Override
    public String toString() {
        return type().toString();
    }
}
