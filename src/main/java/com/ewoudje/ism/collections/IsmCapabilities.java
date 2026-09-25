package com.ewoudje.ism.collections;

import com.ewoudje.ism.Ism;
import com.ewoudje.ism.features.alchemy.AlchemyTableBlockEntity;
import com.ewoudje.ism.features.growth.GrowthCapability;
import com.ewoudje.ism.features.notebook.NotebookContainingCapability;
import com.ewoudje.ism.features.tristitia.TristitiaGrowthHoleBlockEntity;
import com.ewoudje.ism.features.tristitia.growth.SimpleTristitiaGrowth;
import com.ewoudje.ism.features.tristitia.poi.TristitiaCorePOI;
import com.ewoudje.ism.features.tristitia.poi.TristitiaPOI;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.joml.Vector3d;

@EventBusSubscriber
public class IsmCapabilities {
    public static final BlockCapability<TristitiaPOI, Void> TRISTITIA_POI =
            BlockCapability.createVoid(Ism.id("tristitia_poi"), TristitiaPOI.class);

    public static final BlockCapability<GrowthCapability, Void> GROWTH =
            BlockCapability.createVoid(Ism.id("growth"), GrowthCapability.class);

    public static final BlockCapability<NotebookContainingCapability, Void> NOTEBOOK_CONTAINING =
            BlockCapability.createVoid(Ism.id("notebook_container"), NotebookContainingCapability.class);

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlock(
                GROWTH,
                (level, pos, state, be, side) -> SimpleTristitiaGrowth.INSTANCE,
                IsmBlocks.TRISTITIA_VINE_GROWTH.get(),
                IsmBlocks.TRISTITIA_CORE_GROWTH.get(),
                IsmBlocks.TRISTITIA_GRASS_GROWTH.get()
        );

        event.registerBlockEntity(
                TRISTITIA_POI,
                IsmBlockEntities.TRISTITIA_CORE.get(),
                (be, ctx) -> new TristitiaCorePOI(
                        be,
                        new Vector3d(be.getBlockPos().getX() + 0.5, be.getBlockPos().getY() + 0.5, be.getBlockPos().getZ() + 0.5)
                )
        );

        event.registerBlockEntity(
                TRISTITIA_POI,
                IsmBlockEntities.TRISTITIA_SEAL_HOLE.get(),
                TristitiaGrowthHoleBlockEntity::poiCapability
        );

        event.registerBlockEntity(
                NOTEBOOK_CONTAINING,
                IsmBlockEntities.ALCHEMY_TABLE.get(),
                (be, unused) ->
                        new NotebookContainingCapability.ContainerCapability(be, AlchemyTableBlockEntity.NOTEBOOK_SLOT)
        );
    }
}
