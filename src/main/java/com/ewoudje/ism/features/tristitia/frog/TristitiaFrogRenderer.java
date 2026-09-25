package com.ewoudje.ism.features.tristitia.frog;

import com.ewoudje.ism.Ism;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.animal.frog.FrogModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.FrogRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;

public class TristitiaFrogRenderer extends MobRenderer<TristitiaFrogEntity, FrogRenderState, FrogModel> {
    public static final Identifier FROG_TEXTURE = Ism.id("textures/entity/tristitia_frog.png");

    public TristitiaFrogRenderer(EntityRendererProvider.Context context) {
        super(context, new FrogModel(context.bakeLayer(ModelLayers.FROG)), 0.3F);
    }

    public Identifier getTextureLocation(FrogRenderState state) {
        return state.texture;
    }

    public FrogRenderState createRenderState() {
        return new FrogRenderState();
    }

    public void extractRenderState(TristitiaFrogEntity entity, FrogRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.isSwimming = entity.isInWater();
        state.jumpAnimationState.copyFrom(entity.jumpAnimationState);
        state.croakAnimationState.copyFrom(entity.croakAnimationState);
        state.tongueAnimationState.copyFrom(entity.tongueAnimationState);
        state.swimIdleAnimationState.copyFrom(entity.swimIdleAnimationState);
        state.texture = FROG_TEXTURE;
    }

    @Override
    public void submit(FrogRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.scale(2f, 2f, 2f);
        super.submit(state, poseStack, submitNodeCollector, camera);
        poseStack.popPose();
    }
}
