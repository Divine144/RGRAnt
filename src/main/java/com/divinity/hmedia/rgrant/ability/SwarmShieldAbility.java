package com.divinity.hmedia.rgrant.ability;

import com.divinity.hmedia.rgrant.cap.AntHolderAttacher;
import dev._100media.hundredmediaabilities.ability.Ability;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class SwarmShieldAbility extends Ability {

    @Override
    public void executeToggle(ServerLevel level, ServerPlayer player, boolean toggledOn) {
        var holder = AntHolderAttacher.getAntHolderUnwrap(player);
        if (holder != null) {
            double remainingShield = holder.getRemainingShield();
            holder.setRemainingShield(toggledOn ? (remainingShield > 0 ? remainingShield : 50) : 0);
        }
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
    public boolean isToggleAbility() {
        return true;
    }

    @Override
    public int getCooldownDuration() {
        return 20 * 30;
    }
}
