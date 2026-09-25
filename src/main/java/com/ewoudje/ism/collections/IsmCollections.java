package com.ewoudje.ism.collections;

import net.neoforged.bus.api.IEventBus;

public class IsmCollections {

    public static void register(IEventBus modBus) {
        IsmAttachments.REGISTERY.register(modBus);
        IsmBlockEntities.REGISTRY.register(modBus);
        IsmBlocks.REGISTRY.register(modBus);
        IsmCreativeTabs.REGISTRY.register(modBus);
        IsmDataComponents.REGISTRY.register(modBus);
        IsmEntities.REGISTRY.register(modBus);
        IsmItems.REGISTRY.register(modBus);
        IsmMenus.REGISTRY.register(modBus);
        IsmNotes.REGISTRY.register(modBus);
        IsmParticles.REGISTER.register(modBus);
        IsmStructures.REGISTRY.register(modBus);
        IsmStructures.PIECES_REGISTRY.register(modBus);
    }
}
