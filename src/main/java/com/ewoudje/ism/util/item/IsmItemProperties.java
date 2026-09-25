package com.ewoudje.ism.util.item;

import com.ewoudje.ism.mixins.accessors.ItemPropertiesAccessor;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class IsmItemProperties extends Item.Properties {
    boolean shouldShowCreator = false;

    public IsmItemProperties(Identifier key) {
        setId(ResourceKey.create(Registries.ITEM, key));
    }

    public IsmItemProperties loredCreator() {
        shouldShowCreator = true;
        return this;
    }

    @Override
    public IsmItemProperties fireResistant() {
        super.fireResistant();
        return this;
    }

    @Override
    public IsmItemProperties food(FoodProperties foodProperties) {
        super.food(foodProperties);
        return this;
    }

    @Override
    public <T> IsmItemProperties component(DataComponentType<T> type, T value) {
        super.component(type, value);
        return this;
    }

    @Override
    public <T> IsmItemProperties component(Supplier<? extends DataComponentType<T>> componentType, T value) {
        super.component(componentType, value);
        return this;
    }


    public <T> IsmItemProperties component(Supplier<? extends DataComponentType<T>> componentType, Supplier<T> value) {
        ItemPropertiesAccessor accessor = (ItemPropertiesAccessor) this;
        accessor.setComponentInitializer(accessor.getComponentInitializer().andThen(
                (components, context, key) -> {
                    components.set(componentType.get(), value.get());
                }
        ));
        return this;
    }

    @Override
    public IsmItemProperties stacksTo(int max) {
        super.stacksTo(max);
        return this;
    }
}
