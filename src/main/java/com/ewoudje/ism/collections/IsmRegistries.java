package com.ewoudje.ism.collections;

import com.ewoudje.ism.features.note.Note;
import com.ewoudje.ism.util.gui.menu.CustomMenuType;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@EventBusSubscriber
public class IsmRegistries {

    public static final Registry<CustomMenuType<?>> CUSTOM_MENU_TYPES = new RegistryBuilder<>(CustomMenuType.KEY)
            .sync(true)
            .create();

    public static final Registry<Note> NOTES = new RegistryBuilder<>(Note.KEY)
            .sync(true)
            .create();

    @SubscribeEvent
    private static void register(NewRegistryEvent event) {
        event.register(CUSTOM_MENU_TYPES);
        event.register(NOTES);
    }
}
