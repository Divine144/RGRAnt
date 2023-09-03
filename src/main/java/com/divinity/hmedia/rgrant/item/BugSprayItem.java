package com.divinity.hmedia.rgrant.item;

import com.divinity.hmedia.rgrant.RGRAnt;
import com.divinity.hmedia.rgrant.init.SoundInit;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev._100media.hundredmediageckolib.item.animated.AnimatedItemProperties;
import dev._100media.hundredmediageckolib.item.animated.SimpleAnimatedItem;
import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.function.Consumer;

public class BugSprayItem extends SimpleAnimatedItem {

    private boolean isFirstUse = false;

    private final RawAnimation shake = RawAnimation.begin().then("shake", Animation.LoopType.PLAY_ONCE);
    private final RawAnimation spray = RawAnimation.begin().then("spray", Animation.LoopType.PLAY_ONCE);

    public BugSprayItem(AnimatedItemProperties properties) {
        super(properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity entity, InteractionHand hand) {
        if (entity.level().isClientSide) return InteractionResult.CONSUME;
        triggerAnim(playerIn, GeoItem.getOrAssignId(stack, (ServerLevel) playerIn.level()), "controller", "spray");
        entity.level().playSound(null, entity.blockPosition(), SoundInit.BUG_SPRAY.get(), SoundSource.PLAYERS, 0.5f, 1f);
        entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0, false, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0, false, false, false));
        ((ServerLevel) playerIn.level()).sendParticles(ParticleTypes.LARGE_SMOKE, entity.getX(), entity.getEyeY(), entity.getZ(), 10, 0, 0, 0, 0.1f);
        return InteractionResult.CONSUME;
    }


    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null)
                    this.renderer = new GeoItemRenderer<BugSprayItem>(new DefaultedItemGeoModel<>(new ResourceLocation(RGRAnt.MODID, "bug_spray"))) {
                        @Override
                        public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
                            poseStack.pushPose();
                            switch (transformType) {
                                case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
                                    poseStack.translate(0, -0.4, 0);
                                }
                                case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> {
                                    poseStack.translate(0, -0.25, 0);
                                }
                            }
                            super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
                            poseStack.popPose();
                        }

                        @Override
                        protected void renderInGui(ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
                            poseStack.pushPose();
                            poseStack.mulPose(Axis.YP.rotationDegrees(90));
                            poseStack.scale(1.0f, 1.0f, 1.0f);
                            poseStack.translate(0.05, -0.6, 0);
                            super.renderInGui(transformType, poseStack, bufferSource, packedLight, packedOverlay);
                            poseStack.popPose();
                        }
                    };
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, event -> {
            var data = event.getData(DataTickets.ITEMSTACK);
            var entity = event.getData(DataTickets.ENTITY);
            if (data != null && data.getItem() == this && entity instanceof Player player) {

            }
            return PlayState.CONTINUE;
        }).triggerableAnim("spray", spray));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.NONE;
    }
}
