package com.divinity.hmedia.rgrant.item;

import com.divinity.hmedia.rgrant.utils.AntUtils;
import dev._100media.hundredmediageckolib.item.animated.AnimatedItemProperties;
import dev._100media.hundredmediageckolib.item.animated.SimpleAnimatedItem;
import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class BugSprayItem extends SimpleAnimatedItem {

    public BugSprayItem(AnimatedItemProperties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (pLevel.isClientSide) {
            return InteractionResultHolder.pass(itemStack);

        }
        return ItemUtils.startUsingInstantly(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {
        super.onUseTick(pLevel, pLivingEntity, pStack, pRemainingUseDuration);
        if (pLevel.isClientSide || !(pLivingEntity instanceof Player player)) {
            return;
        }
        if (pRemainingUseDuration % 20 == 0) {
            Vec3 look = player.getViewVector(0);
            Vec3 startVec = player.getEyePosition(0);
            Vec3 endVec = startVec.add(look.x * 5, look.y * 5, look.z * 5);
            AABB box = new AABB(startVec, endVec);
            box.inflate(2);
            EntityHitResult result = AntUtils.rayTraceEntities(pLevel, player, startVec, endVec, box, p -> p instanceof LivingEntity entity && MorphHolderAttacher.getCurrentMorph(entity).isPresent());
            if (result != null) {
                Entity entity = result.getEntity();
                if (entity instanceof LivingEntity livingEntity) {
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0));
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0));
                    // TODO: Add sound here
                }
            }

        }
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }
}
