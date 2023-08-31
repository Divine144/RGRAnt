package com.divinity.hmedia.rgrant.ability;

import com.divinity.hmedia.rgrant.cap.AntHolder;
import com.divinity.hmedia.rgrant.cap.AntHolderAttacher;
import dev._100media.hundredmediaabilities.ability.Ability;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class AntSizeUpAbility extends Ability {

    @Override
    public void executePressed(ServerLevel level, ServerPlayer player) {
        var holder = AntHolderAttacher.getAntHolderUnwrap(player);
        if (holder != null) {
            AntHolder.Size size = holder.getCurrentSize();
            if (size == size.next()) {
                player.displayClientMessage(Component.literal("Cannot Increase Size Further").withStyle(ChatFormatting.RED), true);
            }
            else {
                holder.setCurrentSize(size.next());
                player.displayClientMessage(Component.literal("Size Increased!").withStyle(ChatFormatting.GREEN), true);
            }
        }
        super.executePressed(level, player);
    }

    @Override
    public boolean shouldCooldown(ServerLevel level, ServerPlayer player) {
        var holder = AntHolderAttacher.getAntHolderUnwrap(player);
        if (holder != null) {
            return holder.getGigaAntTicks() <= 0;
        }
        return super.shouldCooldown(level, player);
    }

    @Override
    public int getCooldownDuration() {
        return 20 * 5;
    }
}
