package com.ewoudje.ism.client;

import com.ewoudje.ism.Ism;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = Ism.ID, dist = Dist.CLIENT)
public class IsmClient {
    public IsmClient(IEventBus modBus, ModContainer container) {

    }
}
