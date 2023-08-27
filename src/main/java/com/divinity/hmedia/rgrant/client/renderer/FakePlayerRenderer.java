package com.divinity.hmedia.rgrant.client.renderer;

import com.divinity.hmedia.rgrant.client.layer.FakePlayerItemInHandLayer;
import com.divinity.hmedia.rgrant.entity.MindControlledPlayerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

public class FakePlayerRenderer extends LivingEntityRenderer<LivingEntity, PlayerModel<LivingEntity>> {

    private static final ResourceLocation ZOMBIE_LOCATION = new ResourceLocation("textures/entity/zombie/zombie.png");

    public FakePlayerRenderer(EntityRendererProvider.Context pContext) {
        this(pContext, false);
    }

    public FakePlayerRenderer(EntityRendererProvider.Context pContext, boolean pUseSlimModel) {
        super(pContext, new PlayerModel<>(pContext.bakeLayer(pUseSlimModel ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER), pUseSlimModel), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel<>(pContext.bakeLayer(pUseSlimModel ? ModelLayers.PLAYER_SLIM_INNER_ARMOR : ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel<>(pContext.bakeLayer(pUseSlimModel ? ModelLayers.PLAYER_SLIM_OUTER_ARMOR : ModelLayers.PLAYER_OUTER_ARMOR)), pContext.getModelManager()));
        this.addLayer(new FakePlayerItemInHandLayer<>(this, pContext.getItemInHandRenderer()));
        this.addLayer(new ArrowLayer<>(pContext, this));
        this.addLayer(new CustomHeadLayer<>(this, pContext.getModelSet(), pContext.getItemInHandRenderer()));
        this.addLayer(new ElytraLayer<>(this, pContext.getModelSet()));
        this.addLayer(new SpinAttackEffectLayer<>(this, pContext.getModelSet()));
        this.addLayer(new BeeStingerLayer<>(this));
    }

    @ParametersAreNonnullByDefault
    public void render(LivingEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        this.setModelProperties(pEntity);
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull LivingEntity pEntity) {
        if (pEntity instanceof MindControlledPlayerEntity mindControlledPlayerEntity) {
            Player player = mindControlledPlayerEntity.getOwner();
            if (player instanceof AbstractClientPlayer abstractClientPlayer) {
                return abstractClientPlayer.getSkinTextureLocation();
            }
        }
        return ZOMBIE_LOCATION; // temp location as a fallback, since this is nonnull
    }

    @Override
    public @NotNull Vec3 getRenderOffset(LivingEntity pEntity, float pPartialTicks) {
        return pEntity.isCrouching() ? new Vec3(0.0D, -0.125D, 0.0D) : super.getRenderOffset(pEntity, pPartialTicks);
    }

    @Override
    protected void scale(@NotNull LivingEntity pLivingEntity, PoseStack pMatrixStack, float pPartialTickTime) {
        float f = 0.9375F;
        pMatrixStack.scale(f, f, f);
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void renderNameTag(LivingEntity pEntity, Component pDisplayName, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        double d0 = this.entityRenderDispatcher.distanceToSqr(pEntity);
        if (pEntity instanceof MindControlledPlayerEntity mindControlledPlayerEntity) {
            Player player = mindControlledPlayerEntity.getOwner();
            if (player != null) {
                pMatrixStack.pushPose();
                if (d0 < 100.0D) {
                    Scoreboard scoreboard = player.getScoreboard();
                    Objective objective = scoreboard.getDisplayObjective(2);
                    if (objective != null) {
                        Score score = scoreboard.getOrCreatePlayerScore(player.getScoreboardName(), objective);
                        super.renderNameTag(player, Component.literal(Integer.toString(score.getScore())).append(CommonComponents.SPACE).append(objective.getDisplayName()), pMatrixStack, pBuffer, pPackedLight);
                        pMatrixStack.translate(0.0F, 9.0F * 1.15F * 0.025F, 0.0F);
                    }
                }

                super.renderNameTag(pEntity, pDisplayName, pMatrixStack, pBuffer, pPackedLight);
                pMatrixStack.popPose();
            }
        }
    }

    @Override
    protected void setupRotations(@NotNull LivingEntity pEntityLiving, @NotNull PoseStack pMatrixStack, float pAgeInTicks, float pRotationYaw, float pPartialTicks) {
        float f = pEntityLiving.getSwimAmount(pPartialTicks);
        if (pEntityLiving.isFallFlying()) {
            super.setupRotations(pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks);
            float f1 = (float)pEntityLiving.getFallFlyingTicks() + pPartialTicks;
            float f2 = Mth.clamp(f1 * f1 / 100.0F, 0.0F, 1.0F);
            if (!pEntityLiving.isAutoSpinAttack()) {
                pMatrixStack.mulPose(Axis.XP.rotationDegrees(f2 * (-90.0F - pEntityLiving.getXRot())));
            }
            Vec3 vec3 = pEntityLiving.getViewVector(pPartialTicks);
            Vec3 vec31 = pEntityLiving.getDeltaMovement();
            double d0 = vec31.horizontalDistanceSqr();
            double d1 = vec3.horizontalDistanceSqr();
            if (d0 > 0.0D && d1 > 0.0D) {
                double d2 = (vec31.x * vec3.x + vec31.z * vec3.z) / Math.sqrt(d0 * d1);
                double d3 = vec31.x * vec3.z - vec31.z * vec3.x;
                pMatrixStack.mulPose(Axis.YP.rotation((float)(Math.signum(d3) * Math.acos(d2))));
            }
        }
        else if (f > 0.0F) {
            super.setupRotations(pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks);
            float f3 = pEntityLiving.isInWater() || pEntityLiving.isInFluidType((fluidType, height) -> pEntityLiving.canSwimInFluidType(fluidType)) ? -90.0F - pEntityLiving.getXRot() : -90.0F;
            float f4 = Mth.lerp(f, 0.0F, f3);
            pMatrixStack.mulPose(Axis.XP.rotationDegrees(f4));
            if (pEntityLiving.isVisuallySwimming()) {
                pMatrixStack.translate(0.0D, -1.0D, 0.3F);
            }
        }
        else {
            super.setupRotations(pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks);
        }
    }

    private void setModelProperties(LivingEntity pClientPlayer) {
        PlayerModel<LivingEntity> playerModel = this.getModel();
        if (pClientPlayer.isSpectator()) {
            playerModel.setAllVisible(false);
            playerModel.head.visible = true;
            playerModel.hat.visible = true;
        }
        else {
            playerModel.setAllVisible(true);
            playerModel.crouching = pClientPlayer.isCrouching();
            HumanoidModel.ArmPose pose = getArmPose(pClientPlayer, InteractionHand.MAIN_HAND);
            HumanoidModel.ArmPose offPose = getArmPose(pClientPlayer, InteractionHand.OFF_HAND);
            if (pose.isTwoHanded()) {
                offPose = pClientPlayer.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
            }
            if (pClientPlayer.getMainArm() == HumanoidArm.RIGHT) {
                playerModel.rightArmPose = pose;
                playerModel.leftArmPose = offPose;
            }
            else {
                playerModel.rightArmPose = offPose;
                playerModel.leftArmPose = pose;
            }
        }
    }

    private HumanoidModel.ArmPose getArmPose(LivingEntity pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (itemstack.isEmpty()) {
            return HumanoidModel.ArmPose.EMPTY;
        }
        else {
            if (pPlayer.getUsedItemHand() == pHand && pPlayer.getUseItemRemainingTicks() > 0) {
                UseAnim useanim = itemstack.getUseAnimation();
                if (useanim == UseAnim.CROSSBOW && pHand == pPlayer.getUsedItemHand()) {
                    return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                }
                else return switch (useanim) {
                    case BLOCK ->  HumanoidModel.ArmPose.BLOCK;
                    case BOW -> HumanoidModel.ArmPose.BOW_AND_ARROW;
                    case SPEAR -> HumanoidModel.ArmPose.THROW_SPEAR;
                    case SPYGLASS -> HumanoidModel.ArmPose.SPYGLASS;
                    default -> HumanoidModel.ArmPose.TOOT_HORN;
                };
            }
            else if (!pPlayer.swinging && itemstack.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(itemstack)) {
                return HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }
            HumanoidModel.ArmPose forgeArmPose = net.minecraftforge.client.extensions.common.IClientItemExtensions.of(itemstack).getArmPose(pPlayer, pHand, itemstack);
            if (forgeArmPose != null) return forgeArmPose;
            return HumanoidModel.ArmPose.ITEM;
        }
    }
}
