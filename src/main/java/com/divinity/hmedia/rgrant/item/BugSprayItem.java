package com.divinity.hmedia.rgrant.item;

import com.divinity.hmedia.rgrant.RGRAnt;
import com.divinity.hmedia.rgrant.init.SoundInit;
import com.divinity.hmedia.rgrant.utils.AntUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev._100media.hundredmediageckolib.item.animated.AnimatedItemProperties;
import dev._100media.hundredmediageckolib.item.animated.SimpleAnimatedItem;
import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
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
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (pLevel.isClientSide) {
            return InteractionResultHolder.consume(itemStack);

        }
        BlockHitResult result = AntUtils.blockTrace(pPlayer, ClipContext.Fluid.NONE, 5, false);
        if (result != null) {
            AABB aabb = AABB.ofSize(result.getLocation(), 4, 4, 4);
            var list = AntUtils.getEntitiesInRange(pPlayer, LivingEntity.class, 15, 15, 15, p -> p != pPlayer && MorphHolderAttacher.getCurrentMorph(p).isPresent() || !(p instanceof Player));
            BlockPos.betweenClosedStream(aabb).forEach(blockPos -> {
                for (var ent : list) {
                    if (blockPos.closerToCenterThan(ent.position(), 2)) {
                        if (!ent.hasEffect(MobEffects.POISON)) {
                            ent.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0, false, false, false));
                            ent.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0, false, false, false));
                        }
                    }
                }
                ((ServerLevel) pLevel).sendParticles(ParticleTypes.LARGE_SMOKE, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 4, 0, 0, 0, 0);
            });
            triggerAnim(pPlayer, GeoItem.getOrAssignId(itemStack, (ServerLevel) pPlayer.level()), "controller", "spray");
            pPlayer.level().playSound(null, pPlayer.blockPosition(), SoundInit.BUG_SPRAY.get(), SoundSource.PLAYERS, 0.5f, 1f);
        }
        return InteractionResultHolder.consume(itemStack);
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
                return event.setAndContinue(shake);
            }
            return PlayState.CONTINUE;
        }).triggerableAnim("spray", spray));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }
}
