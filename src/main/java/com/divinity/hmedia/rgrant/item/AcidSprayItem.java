package com.divinity.hmedia.rgrant.item;

import com.divinity.hmedia.rgrant.init.EntityInit;
import com.divinity.hmedia.rgrant.init.SoundInit;
import com.divinity.hmedia.rgrant.utils.AntUtils;
import dev._100media.hundredmediageckolib.item.animated.AnimatedItemProperties;
import dev._100media.hundredmediageckolib.item.animated.SimpleAnimatedItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class AcidSprayItem extends Item {
    public AcidSprayItem(Item.Properties properties) {
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
            if (pLivingEntity instanceof ServerPlayer player) {
                if (pRemainingUseDuration % 2 == 0) {
                    var entity = EntityInit.ACID_ENTITY.get().create(pLevel);
                    if (entity != null) {
                        entity.setPos(player.getX(), player.getEyeY() - 0.15, player.getZ());
                        entity.setOwner(player);
                        entity.setNoGravity(true);
                        entity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5F, 0);
                        entity.setYRot(-Mth.wrapDegrees(player.getYRot()));
                        entity.setXRot(-Mth.wrapDegrees(player.getXRot()));
                        entity.xRotO = -Mth.wrapDegrees(player.xRotO);
                        entity.yRotO = -Mth.wrapDegrees(player.yRotO);
                        player.level().addFreshEntity(entity);
                        player.level().playSound(null, player.blockPosition(), SoundInit.ACID_SPRAY.get(), SoundSource.PLAYERS, 0.5f, 1f);
                    }
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
