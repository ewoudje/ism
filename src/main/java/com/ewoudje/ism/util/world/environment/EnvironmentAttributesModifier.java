package com.ewoudje.ism.util.world.environment;

import com.ewoudje.ism.collections.IsmAttachments;
import com.ewoudje.ism.collections.IsmAttributes;
import com.ewoudje.ism.collections.IsmEnvironmentAttributes;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.List;

@EventBusSubscriber
public record EnvironmentAttributesModifier(
        List<Entry> entries
) {

    public static final Codec<EnvironmentAttributesModifier> CODEC = Entry.CODEC.listOf()
            .xmap(EnvironmentAttributesModifier::new, EnvironmentAttributesModifier::entries);

    public Multimap<Holder<Attribute>, AttributeModifier> toMap() {
        ImmutableListMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableListMultimap.builder();

        for (Entry entry : entries) {
            builder.put(entry.attribute(), entry.modifier());
        }

        return builder.build();
    }

    public record Entry(Holder<Attribute> attribute, AttributeModifier modifier) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(i -> i.group(
                Attribute.CODEC.fieldOf("type").forGetter(Entry::attribute),
                AttributeModifier.MAP_CODEC.forGetter(Entry::modifier)
        ).apply(i, Entry::new));
    }

    @SubscribeEvent
    private static void applyModifiers(EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;

        Level level = entity.level();
        AttributeMap attributes = entity.getAttributes();

        EnvironmentAttributesModifier modifiers = level.environmentAttributes().getValue(
                IsmEnvironmentAttributes.ATTRIBUTE_MODIFIER.get(),
                entity.position()
        );
        EnvironmentAttributesModifier prevModifiers =
                entity.getExistingDataOrNull(IsmAttachments.LAST_ENVIRONMENT_MODIFIERS);

        if (modifiers == prevModifiers) return;

        if (prevModifiers != null) {
            attributes.removeAttributeModifiers(prevModifiers.toMap());
        }

        attributes.addTransientAttributeModifiers(modifiers.toMap());
        entity.setData(IsmAttachments.LAST_ENVIRONMENT_MODIFIERS, modifiers);
    }
}
