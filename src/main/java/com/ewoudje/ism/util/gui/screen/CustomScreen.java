package com.ewoudje.ism.util.gui.screen;

import com.ewoudje.ism.util.gui.menu.CustomMenu;

public interface CustomScreen<T extends CustomMenu> {
    T menu();
}
