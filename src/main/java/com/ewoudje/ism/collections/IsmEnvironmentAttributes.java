package com.ewoudje.ism.collections;

import com.ewoudje.ism.Ism;
import com.ewoudje.ism.util.world.environment.EnvironmentAttributesModifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.attribute.AttributeType;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

public class IsmEnvironmentAttributes {
    public static final DeferredRegister<EnvironmentAttribute<?>> REGISTRY = DeferredRegister.create(Registries.ENVIRONMENT_ATTRIBUTE, Ism.ID);
    public static final DeferredRegister<AttributeType<?>> TYPE_REGISTRY = DeferredRegister.create(Registries.ATTRIBUTE_TYPE, Ism.ID);
    public static final AttributeType<EnvironmentAttributesModifier> ATTRIBUTE_MODIFIER_TYPE =
            AttributeType.ofNotInterpolated(EnvironmentAttributesModifier.CODEC);

    static {
        TYPE_REGISTRY.register("attribute_modifier", () -> ATTRIBUTE_MODIFIER_TYPE);
    }

    public static final Supplier<EnvironmentAttribute<EnvironmentAttributesModifier>> ATTRIBUTE_MODIFIER =
            REGISTRY.register("attribute_modifier", () ->
                    EnvironmentAttribute.builder(ATTRIBUTE_MODIFIER_TYPE)
                            .defaultValue(new EnvironmentAttributesModifier(List.of()))
                            .syncable()
                            .build()
            );
}
