package com.ewoudje.ism.collections;

import com.ewoudje.ism.Ism;
import com.ewoudje.ism.features.tristitia.frog.TristitiaFrogEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@EventBusSubscriber
public class IsmEntities {
    public static final DeferredRegister.Entities REGISTRY = DeferredRegister.createEntities(Ism.ID);

    public static final Supplier<EntityType<TristitiaFrogEntity>> TRISTITIA_FROG = REGISTRY.registerEntityType(
            "tristitia_frog",
            TristitiaFrogEntity::new,
            MobCategory.MONSTER,
            b -> b
                    .sized(1f, 0.9f)
                    .eyeHeight(0.5f)
                    .notInPeaceful()
    );

    @SubscribeEvent
    public static void entityAttributes(EntityAttributeCreationEvent event) {
        event.put(TRISTITIA_FROG.get(), TristitiaFrogEntity.createAttributes().build());
    }
}
