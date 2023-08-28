package com.divinity.hmedia.rgrant.item;

import dev._100media.hundredmediageckolib.item.animated.AnimatedItemProperties;
import dev._100media.hundredmediageckolib.item.animated.SimpleAnimatedItem;
import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import org.jetbrains.annotations.NotNull;

public class BugSprayItem extends SimpleAnimatedItem {

    public BugSprayItem(AnimatedItemProperties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity entity, InteractionHand hand) {
        if (entity.level().isClientSide) return InteractionResult.SUCCESS;
        if (entity instanceof ServerPlayer player && MorphHolderAttacher.getCurrentMorph(player).isPresent()) {
            player.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0));
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
            // TODO: Add sound here
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.NONE;
    }
}
