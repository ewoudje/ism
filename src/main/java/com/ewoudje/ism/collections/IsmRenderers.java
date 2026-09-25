package com.ewoudje.ism.collections;

import com.ewoudje.ism.features.alchemy.AlchemyTableBlockEntityRenderer;
import com.ewoudje.ism.features.tristitia.frog.TristitiaFrogRenderer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber
public class IsmRenderers {

    @SubscribeEvent
    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(IsmBlockEntities.ALCHEMY_TABLE.get(), AlchemyTableBlockEntityRenderer::new);

        event.registerEntityRenderer(IsmEntities.TRISTITIA_FROG.get(), TristitiaFrogRenderer::new);
    }
}
