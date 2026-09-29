package com.ewoudje.ism.collections;

import com.ewoudje.ism.Ism;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.PercentageAttribute;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@EventBusSubscriber
public class IsmAttributes {

    public static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(Registries.ATTRIBUTE, Ism.ID);

    public static final DeferredHolder<Attribute, Attribute> SADNESS = REGISTRY.register("sadness", () -> new PercentageAttribute(
            "attribute.ism.sadness",
            0, 0, 1
    ).setSentiment(Attribute.Sentiment.NEGATIVE).setSyncable(true));

    public static final DeferredHolder<Attribute, Attribute> SANITY = REGISTRY.register("sanity", () -> new PercentageAttribute(
            "attribute.ism.sanity",
            1, 0, 1
    ).setSyncable(true));

    @SubscribeEvent
    private static void addAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityTypes.PLAYER, SADNESS);
        event.add(EntityTypes.PLAYER, SANITY);
    }
}
