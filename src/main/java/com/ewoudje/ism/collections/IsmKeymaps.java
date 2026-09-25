package com.ewoudje.ism.collections;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.util.Lazy;

@EventBusSubscriber
public class IsmKeymaps {
    public static final Lazy<KeyMapping> ADD_TO_NOTEBOOK = Lazy.of(() -> new KeyMapping(
            "key.ism.add_to_notebook",
            InputConstants.Type.KEYBOARD,
            InputConstants.KEY_G,
            KeyMapping.Category.MISC
    ));

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(ADD_TO_NOTEBOOK.get());
    }
}
