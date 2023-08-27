package com.divinity.hmedia.rgrant.quest.goal;

import dev._100media.hundredmediaquests.goal.KillSpecificTypeGoal;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

public class KillEntityCritGoal extends KillSpecificTypeGoal {

    public KillEntityCritGoal(int target, EntityType<?> type) {
        super(target, type);
    }

    @Override
    public boolean tallyKill(Entity entity, DamageSource source, Player killer, boolean direct) {
        float f2 = killer.getAttackStrengthScale(0.5F);
        boolean flag = f2 > 0.9F;
        boolean canCrit = flag && killer.fallDistance > 0.0F && !killer.onGround() && !killer.onClimbable() && !killer.isInWater() && !killer.hasEffect(MobEffects.BLINDNESS) && !killer.isPassenger();
        canCrit = canCrit && !killer.isSprinting();
        if (canCrit) {
            return super.tallyKill(entity, source);
        }
        return false;
    }

    @Override
    public String getDescriptionId() {
        return "quest.goal.rgrant.kill_entity_crit_goal";
    }
}
