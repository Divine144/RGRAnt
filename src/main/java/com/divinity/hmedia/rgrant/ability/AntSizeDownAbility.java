package com.divinity.hmedia.rgrant.ability;

import com.divinity.hmedia.rgrant.cap.AntHolder;
import com.divinity.hmedia.rgrant.cap.AntHolderAttacher;
import dev._100media.hundredmediaabilities.ability.Ability;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class AntSizeDownAbility extends Ability {

    @Override
    public void executePressed(ServerLevel level, ServerPlayer player) {
        var holder = AntHolderAttacher.getAntHolderUnwrap(player);
        if (holder != null) {
            AntHolder.Size size = holder.getCurrentSize();
            if (size == size.previous()) {
                player.displayClientMessage(Component.literal("Cannot Decrease Size Further").withStyle(ChatFormatting.RED), true);
            }
            else {
                holder.setCurrentSize(size.previous());
                player.refreshDimensions();
                player.displayClientMessage(Component.literal("Size Decreased!").withStyle(ChatFormatting.GREEN), true);
            }
        }
        super.executePressed(level, player);
    }
}
