package com.divinity.hmedia.rgrant.item;

import com.divinity.hmedia.rgrant.utils.AntUtils;
import dev._100media.hundredmediageckolib.item.animated.AnimatedItemProperties;
import dev._100media.hundredmediageckolib.item.animated.SimpleAnimatedItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class AcidSprayItem extends SimpleAnimatedItem {
    public AcidSprayItem(AnimatedItemProperties properties) {
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
        if (!pLevel.isClientSide) {
            EntityHitResult result = AntUtils.rayTraceEntities(pLevel, pLivingEntity, 5, p -> p instanceof LivingEntity);
            if (result != null) {
                if (result.getEntity() instanceof LivingEntity living && living.tickCount % 20 == 0) {
                    if (!living.hasEffect(MobEffects.POISON)) {
                        living.addEffect(new MobEffectInstance(MobEffects.POISON, 600, 0));
                    }
                    living.getArmorSlots().forEach(s -> {
                        if (s.getItem() instanceof ArmorItem) {
                            s.setDamageValue(s.getDamageValue() - 50);
                        }
                    });
                }
            }
        }
        super.onUseTick(pLevel, pLivingEntity, pStack, pRemainingUseDuration);
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
