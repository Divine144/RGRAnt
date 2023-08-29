package com.divinity.hmedia.rgrant.quest.goal;

import com.divinity.hmedia.rgrant.init.ItemInit;
import com.divinity.hmedia.rgrant.utils.AntUtils;
import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import dev._100media.hundredmediaquests.goal.KillPlayersGoal;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public class KillPlayersVenomousStingGoal extends KillPlayersGoal {
    public KillPlayersVenomousStingGoal(int target) {
        super(target);
    }

    @Override
    public boolean tallyKill(Entity entity, DamageSource source) {
        return source.getDirectEntity() instanceof ServerPlayer player && MorphHolderAttacher.getCurrentMorph(player).isPresent()
                && AntUtils.hasItemEitherHands(player, ItemInit.VENOMOUS_STING.get()) && super.tallyKill(entity, source);
    }

    @Override
    public String getDescriptionId() {
        return "quest.goal.rgrant.kill_players_sting_goal";
    }
}
