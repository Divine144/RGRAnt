package com.divinity.hmedia.rgrant.ability;

import com.divinity.hmedia.rgrant.init.EntityInit;
import dev._100media.hundredmediaabilities.ability.Ability;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class AntArmyAbility extends Ability {

    @Override
    public void executePressed(ServerLevel level, ServerPlayer player) {
        for (int i = 0; i < 10; i++) {
            var entity = EntityInit.ANT_ENTITY.get().create(level);
            if (entity != null) {
                entity.setPos(player.position());
                entity.setOwnerUUID(player.getUUID());
                level.addFreshEntity(entity);
            }
        }
        super.executePressed(level, player);
    }

    @Override
    public int getCooldownDuration() {
        return 20 * 10;
    }
}
