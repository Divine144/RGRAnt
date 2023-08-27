package com.divinity.hmedia.rgrant.ability;

import dev._100media.hundredmediaabilities.ability.Ability;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class AntSizeUpAbility extends Ability {

    @Override
    public void executePressed(ServerLevel level, ServerPlayer player) {

        super.executePressed(level, player);
    }

    @Override
    public int getCooldownDuration() {
        return 20 * 5;
    }
}
