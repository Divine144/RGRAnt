package com.divinity.hmedia.rgrant.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.ParametersAreNonnullByDefault;

public class FakePlayerItemInHandLayer<T extends LivingEntity, M extends EntityModel<T> & ArmedModel & HeadedModel> extends ItemInHandLayer<T, M> {

    private final ItemInHandRenderer itemInHandRenderer;

    public FakePlayerItemInHandLayer(RenderLayerParent<T, M> parent, ItemInHandRenderer renderer) {
        super(parent, renderer);
        this.itemInHandRenderer = renderer;
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void renderArmWithItem(LivingEntity pLivingEntity, ItemStack pItemStack, ItemDisplayContext pTransformType, HumanoidArm pArm, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        if (pItemStack.is(Items.SPYGLASS) && pLivingEntity.getUseItem() == pItemStack && pLivingEntity.swingTime == 0) {
            this.renderArmWithSpyglass(pLivingEntity, pItemStack, pArm, pPoseStack, pBuffer, pPackedLight);
        }
        else {
            super.renderArmWithItem(pLivingEntity, pItemStack, pTransformType, pArm, pPoseStack, pBuffer, pPackedLight);
        }
    }

    private void renderArmWithSpyglass(LivingEntity p_174518_, ItemStack p_174519_, HumanoidArm p_174520_, PoseStack pPoseStack, MultiBufferSource pBuffer, int p_174523_) {
        pPoseStack.pushPose();
        ModelPart modelpart = this.getParentModel().getHead();
        float f = modelpart.xRot;
        modelpart.xRot = Mth.clamp(modelpart.xRot, (-(float)Math.PI / 6F), ((float)Math.PI / 2F));
        modelpart.translateAndRotate(pPoseStack);
        modelpart.xRot = f;
        CustomHeadLayer.translateToHead(pPoseStack, false);
        boolean flag = p_174520_ == HumanoidArm.LEFT;
        pPoseStack.translate((flag ? -2.5F : 2.5F) / 16.0F, -0.0625D, 0.0D);
        this.itemInHandRenderer.renderItem(p_174518_, p_174519_, ItemDisplayContext.HEAD, false, pPoseStack, pBuffer, p_174523_);
        pPoseStack.popPose();
    }
}
