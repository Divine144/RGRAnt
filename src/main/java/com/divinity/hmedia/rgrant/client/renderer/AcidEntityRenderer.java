package com.divinity.hmedia.rgrant.client.renderer;

import com.divinity.hmedia.rgrant.RGRAnt;
import com.divinity.hmedia.rgrant.entity.AcidProjectileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev._100media.hundredmediageckolib.client.model.SimpleGeoEntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AcidEntityRenderer extends GeoEntityRenderer<AcidProjectileEntity> {

    public AcidEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SimpleGeoEntityModel<>(RGRAnt.MODID, "acid_entity"));
    }

    @Override
    public void render(AcidProjectileEntity entity, float entityYaw, float partialTick, PoseStack pMatrixStack, MultiBufferSource bufferSource, int packedLight) {
        pMatrixStack.pushPose();
        pMatrixStack.mulPose(Axis.YN.rotationDegrees(Mth.lerp(partialTick, -entity.yRotO, -entity.getYRot()) - 90.0F));
        pMatrixStack.mulPose(Axis.ZN.rotationDegrees(Mth.lerp(partialTick, -entity.xRotO,-entity.getXRot())));
        pMatrixStack.mulPose(Axis.YP.rotationDegrees(-90));
        super.render(entity, entityYaw, partialTick, pMatrixStack, bufferSource, packedLight);
        pMatrixStack.popPose();
    }
}
