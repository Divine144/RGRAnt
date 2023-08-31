package com.divinity.hmedia.rgrant.item;

import com.divinity.hmedia.rgrant.cap.AntHolderAttacher;
import com.divinity.hmedia.rgrant.init.EntityInit;
import com.divinity.hmedia.rgrant.init.ItemInit;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
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
                var fakePlayer = EntityInit.MIND_CONTROLLED_PLAYER.get().create(serverPlayer.serverLevel());
                if (fakePlayer != null) {
                    fakePlayer.copyPosition(serverPlayer);
                    fakePlayer.setOwnerUUID(serverPlayer.getUUID());
                    serverPlayer.setGameMode(GameType.SPECTATOR);
                    holder.setMindControlTicks(1200);
                    for (EquipmentSlot slot : EquipmentSlot.values()) {
                        fakePlayer.setItemSlot(slot, serverPlayer.getItemBySlot(slot));
                    }
                    fakePlayer.setItemSlot(EquipmentSlot.HEAD, ItemInit.MIND_CONTROL_ARMOR.get().getDefaultInstance());
                    serverPlayer.serverLevel().addFreshEntity(fakePlayer);
                    // TODO: Add sound
                }
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
