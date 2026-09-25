package com.ewoudje.ism.features.alchemy;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class AlchemyTableBlockEntityRenderer implements BlockEntityRenderer<AlchemyTableBlockEntity, AlchemyTableBlockEntityRenderer.State> {
    private final SpriteGetter sprites;
    private final ItemModelResolver itemModelResolver;


    public AlchemyTableBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.sprites = context.sprites();
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(
            AlchemyTableBlockEntity blockEntity,
            State state,
            float partialTicks,
            Vec3 cameraPosition,
            ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.yRot = blockEntity.getVisualRotationYInDegrees();
        itemModelResolver.updateForTopItem(
                state.bookState,
                blockEntity.getItem(AlchemyTableBlockEntity.NOTEBOOK_SLOT),
                ItemDisplayContext.NONE,
                blockEntity.getLevel(),
                blockEntity,
                0
        );
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        if (!state.bookState.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 1.15F, 0.5F);
            poseStack.rotate(Axis.YP.rotationDegrees(-state.yRot));
            poseStack.translate(0.22F, 0f, 0f);

            poseStack.rotate(Axis.YP.rotationDegrees(46f));
            poseStack.rotate(Axis.XP.rotationDegrees(44f));
            poseStack.translate(0.0F, 0.F, 0.05F);

            state.bookState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }

    public static class State extends BlockEntityRenderState {
        public final ItemStackRenderState bookState = new ItemStackRenderState();
        public float yRot;
    }
}
