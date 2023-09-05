package com.divinity.hmedia.rgrant.ability;

import com.divinity.hmedia.rgrant.cap.AntHolderAttacher;
import com.divinity.hmedia.rgrant.utils.AntUtils;
import dev._100media.hundredmediaabilities.ability.Ability;
import dev._100media.hundredmediaabilities.capability.AbilityHolderAttacher;
import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.common.ForgeMod;

public class GigaAntAbility extends Ability {

    @Override
    public void executeToggle(ServerLevel level, ServerPlayer player, boolean toggledOn) {
        var holder = AntHolderAttacher.getAntHolderUnwrap(player);
        if (holder != null) {
            var reachDistance = player.getAttribute(ForgeMod.BLOCK_REACH.get());
            var attackDistance = player.getAttribute(ForgeMod.ENTITY_REACH.get());
            if (toggledOn) {
                player.serverLevel().getPlayers(p -> !p.hasEffect(MobEffects.GLOWING) && MorphHolderAttacher.getCurrentMorph(p).isEmpty())
                        .forEach(p -> p.addEffect(new MobEffectInstance(MobEffects.GLOWING, -1, 0, false, false, false)));
                holder.setGigaAntTicks(20 * 60);
                var speed = player.getEffect(MobEffects.MOVEMENT_SPEED);
                var dmg = player.getEffect(MobEffects.DAMAGE_BOOST);
                var jump = player.getEffect(MobEffects.JUMP);
                if (speed != null && dmg != null && jump != null) {
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, speed.getAmplifier() * 2 + 1, false, false ,false));
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, dmg.getAmplifier() * 2 + 1, false, false ,false));
                    player.addEffect(new MobEffectInstance(MobEffects.JUMP, -1, jump.getAmplifier() * 2 + 1, false, false ,false));
                }
                if (reachDistance != null && attackDistance != null) {
                    reachDistance.setBaseValue(reachDistance.getBaseValue() + 20);
                    attackDistance.setBaseValue(attackDistance.getBaseValue() + 20);
                }
            }
            else {
                player.serverLevel().getPlayers(p -> p.hasEffect(MobEffects.GLOWING) && MorphHolderAttacher.getCurrentMorph(p).isEmpty())
                        .forEach(p -> p.removeEffect(MobEffects.GLOWING));
                holder.setGigaAntTicks(0);
                var speed = player.getEffect(MobEffects.MOVEMENT_SPEED);
                var dmg = player.getEffect(MobEffects.DAMAGE_BOOST);
                var jump = player.getEffect(MobEffects.JUMP);
                if (speed != null && dmg != null && jump != null) {
                    player.removeEffect(MobEffects.MOVEMENT_SPEED);
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, speed.getAmplifier() / 2, false, false ,false));
                    player.removeEffect(MobEffects.DAMAGE_BOOST);
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, dmg.getAmplifier() / 2, false, false ,false));
                    player.removeEffect(MobEffects.JUMP);
                    player.addEffect(new MobEffectInstance(MobEffects.JUMP, -1, jump.getAmplifier() / 2, false, false ,false));
                }
                if (reachDistance != null && attackDistance != null) {
                    reachDistance.setBaseValue(Math.max(reachDistance.getAttribute().getDefaultValue(), reachDistance.getBaseValue() - 20));
                    attackDistance.setBaseValue(Math.max(attackDistance.getAttribute().getDefaultValue(), attackDistance.getBaseValue() - 20));
                }
            }
        }
        super.executeToggle(level, player, toggledOn);
    }

    @Override
    public boolean isToggleAbility() {
        return true;
    }

    @Override
    public int getCooldownDuration() {
        return 20;
    }
}
