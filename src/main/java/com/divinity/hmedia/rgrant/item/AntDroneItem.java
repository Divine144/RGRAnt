package com.divinity.hmedia.rgrant.item;

import com.divinity.hmedia.rgrant.entity.AntDroneEntity;
import com.divinity.hmedia.rgrant.init.EntityInit;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class AntDroneItem extends Item {
    private boolean isAttack = true;

    public AntDroneItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (pLevel.isClientSide) {
            return InteractionResultHolder.consume(itemStack);
        }
        CompoundTag tag = itemStack.getOrCreateTag();
        if (pPlayer.isShiftKeyDown()) {
            isAttack = !this.isAttack;
            tag.putBoolean("isAttack", isAttack);
            pPlayer.displayClientMessage(Component.literal("%s mode.".formatted(isAttack ? "ATTACK" : "DEFENSE"))
                    .withStyle(ChatFormatting.GREEN), false);
        }
        return InteractionResultHolder.pass(itemStack);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        ItemStack stack = pContext.getItemInHand();
        Level level = pContext.getLevel();
        Player player = pContext.getPlayer();
        BlockPos blockpos = pContext.getClickedPos().relative(pContext.getClickedFace());
        if (pContext.getClickedFace() == Direction.UP) {
            if (player instanceof ServerPlayer serverPlayer) {
                CompoundTag tag = stack.getOrCreateTag();
                AntDroneEntity entity = new AntDroneEntity(EntityInit.ANT_DRONE_ENTITY.get(), serverPlayer.serverLevel());
                entity.setIsAttack(tag.getBoolean("isAttack"));
                entity.setPos(blockpos.getX(), blockpos.getY(), blockpos.getZ());
                serverPlayer.level().addFreshEntity(entity);
                stack.shrink(1);
                return InteractionResult.CONSUME;
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }
}
