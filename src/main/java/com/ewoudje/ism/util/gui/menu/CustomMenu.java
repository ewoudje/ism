package com.ewoudje.ism.util.gui.menu;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueOutput;

public interface CustomMenu {
    Player player();

    boolean isClosed();

    void onClose();

    void toClientData(ValueOutput output);

    CustomMenuType<?> type();
}
