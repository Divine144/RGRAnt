package com.divinity.hmedia.rgrant.item;

import com.divinity.hmedia.rgrant.cap.AntHolderAttacher;
import com.divinity.hmedia.rgrant.entity.MindControlledPlayerEntity;
import com.divinity.hmedia.rgrant.init.EntityInit;
import dev._100media.hundredmediageckolib.item.animated.AnimatedItemProperties;
import dev._100media.hundredmediageckolib.item.animated.SimpleAnimatedItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.NotNull;

public class MindControlItem extends Item {

    public MindControlItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity entity, InteractionHand hand) {
        if (entity.level().isClientSide) return InteractionResult.SUCCESS;
        if (entity instanceof ServerPlayer serverPlayer) {
            var holder = AntHolderAttacher.getAntHolderUnwrap(serverPlayer);
            if (holder != null && !holder.isMindControlled()) {
                MindControlledPlayerEntity fakePlayer = new MindControlledPlayerEntity(EntityInit.MIND_CONTROLLED_PLAYER.get(), serverPlayer.serverLevel());
                fakePlayer.copyPosition(serverPlayer);
                serverPlayer.setGameMode(GameType.SPECTATOR);
                fakePlayer.setOwnerUUID(serverPlayer.getUUID());
                holder.setMindControlTicks(1200);
                serverPlayer.serverLevel().addFreshEntity(fakePlayer);
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.NONE;
    }
}
