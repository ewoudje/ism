package com.ewoudje.ism.collections;

import com.ewoudje.ism.Ism;
import com.ewoudje.ism.features.tristitia.client.ClingyGrowthModelDefinition;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterBlockStateModels;

@EventBusSubscriber
public class IsmBlockModelDefinitions {


    @SubscribeEvent
    private static void registerDefinitions(RegisterBlockStateModels event) {
        event.registerDefinition(Ism.id("clingy_growth"), ClingyGrowthModelDefinition.MAP_CODEC);
    }
}
