package com.ewoudje.ism.util.gui;

import net.minecraft.world.entity.player.Player;

public class AbstractCustomMenu implements CustomMenu {
    protected final Player player;

    public AbstractCustomMenu(Player player) {
        this.player = player;
    }

    protected boolean isClientSide() {
        return player.level().isClientSide();
    }
}
