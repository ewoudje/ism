package com.ewoudje.ism.collections;

import com.ewoudje.ism.Ism;
import com.ewoudje.ism.features.notebook.NotebookMenu;
import com.ewoudje.ism.features.notebook.NotebookScreen;
import com.ewoudje.ism.util.gui.menu.CustomMenu;
import com.ewoudje.ism.util.gui.menu.CustomMenuType;
import com.ewoudje.ism.util.gui.menu.ServerMenuUtil;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class IsmMenus {
    public static final DeferredRegister<CustomMenuType<?>> REGISTRY = DeferredRegister.create(CustomMenuType.KEY, Ism.ID);

    public static final Supplier<CustomMenuType<NotebookMenu>> NOTEBOOK = REGISTRY.register("notebook", CustomMenuType.create(
            NotebookMenu::new,
            m -> new NotebookScreen(m, Component.translatable("ism.notebook.title"))
    ));

    public static void open(CustomMenu menu) {
        if (menu.player().level().isClientSide()) return;
        ServerMenuUtil.openMenu(menu);
    }
}
