package com.ewoudje.ism.collections;

import com.ewoudje.ism.features.alchemy.AlchemyTableBlockEntityRenderer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber
public class IsmBlockEntityRenderers {

    @SubscribeEvent
    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(IsmBlockEntities.ALCHEMY_TABLE.get(), AlchemyTableBlockEntityRenderer::new);
    }
}
