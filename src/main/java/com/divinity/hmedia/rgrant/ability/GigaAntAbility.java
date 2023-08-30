package com.divinity.hmedia.rgrant.ability;

import com.divinity.hmedia.rgrant.cap.AntHolderAttacher;
import com.divinity.hmedia.rgrant.utils.AntUtils;
import dev._100media.hundredmediaabilities.ability.Ability;
import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
                AntUtils.amplifyCurrentEffect(player, false, MobEffects.MOVEMENT_SPEED, MobEffects.DAMAGE_BOOST, MobEffects.JUMP);
                if (reachDistance != null && attackDistance != null) {
                    reachDistance.setBaseValue(reachDistance.getBaseValue() + 20);
                    attackDistance.setBaseValue(attackDistance.getBaseValue() + 20);
                }
            }
            else {
                player.serverLevel().getPlayers(p -> p.hasEffect(MobEffects.GLOWING) && MorphHolderAttacher.getCurrentMorph(p).isEmpty())
                        .forEach(p -> p.removeEffect(MobEffects.GLOWING));
                holder.setGigaAntTicks(0);
                AntUtils.amplifyCurrentEffect(player, true, MobEffects.MOVEMENT_SPEED, MobEffects.DAMAGE_BOOST, MobEffects.JUMP);
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
        return 20 * 60;
    }
}
