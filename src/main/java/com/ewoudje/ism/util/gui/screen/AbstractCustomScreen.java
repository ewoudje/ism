package com.ewoudje.ism.util.gui.screen;

import com.ewoudje.ism.util.gui.menu.ClientMenuUtil;
import com.ewoudje.ism.util.gui.menu.CustomMenu;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class AbstractCustomScreen<T extends CustomMenu> extends Screen implements CustomScreen<T> {
    protected final T menu;

    protected AbstractCustomScreen(T menu, Component title) {
        super(title);
        this.menu = menu;
    }


    @Override
    public T menu() {
        return menu;
    }

    @Override
    public boolean isInGameUi() {
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        super.onClose();
        ClientMenuUtil.closeMenu(menu);
    }
}
