package com.koenigsegg.hypercars.client.renderer;

import com.koenigsegg.hypercars.client.model.KoenigseggGeoModel;
import com.koenigsegg.hypercars.entity.KoenigseggEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class KoenigseggRenderer extends GeoEntityRenderer<KoenigseggEntity> {
    public KoenigseggRenderer(EntityRendererProvider.Context context) {
        super(context, new KoenigseggGeoModel());
        this.shadowRadius = 1.65f;
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public void render(KoenigseggEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-entityYaw));
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    @Override
    public RenderType getRenderType(KoenigseggEntity animatable, ResourceLocation texture,
                                    MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}
