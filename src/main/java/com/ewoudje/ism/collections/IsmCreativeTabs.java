package com.ewoudje.ism.collections;

import com.ewoudje.ism.Ism;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class IsmCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Ism.ID);

    public static final Supplier<CreativeModeTab> ISM_TAB = REGISTRY.register("ism",
            () -> CreativeModeTab.builder()
                    .title(Component.literal("ISM"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> IsmItems.NOTEBOOK.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        IsmItems.addToIsmTab(output);
                    })
                    .build());
}
