package com.divinity.hmedia.rgrant.mixin;

import com.divinity.hmedia.rgrant.quest.goal.GoatLoseHornGoal;
import com.divinity.hmedia.rgrant.utils.AntUtils;
import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.behavior.RamTarget;
import net.minecraft.world.entity.animal.goat.Goat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RamTarget.class)
public class RamTargetMixin {

    @Inject(
            method = "tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/animal/goat/Goat;J)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;playSound(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V",
                    ordinal = 2,
                    shift = At.Shift.AFTER
            )
    )
    public void tick(ServerLevel pLevel, Goat pOwner, long pGameTime, CallbackInfo ci) {
        var list = AntUtils.getEntitiesInRange(pOwner, ServerPlayer.class, 10, 10, 10, p -> MorphHolderAttacher.getCurrentMorph(p).isPresent());
        if (!list.isEmpty()) {
            ServerPlayer player = list.get(0);
            AntUtils.addToGenericQuestGoal(player, GoatLoseHornGoal.class);
        }
    }
}
