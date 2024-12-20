package com.divinity.hmedia.rgrant.mixin;

import com.divinity.hmedia.rgrant.quest.goal.CamelJumpGoal;
import com.divinity.hmedia.rgrant.utils.AntUtils;
import dev._100media.hundredmediamorphs.common.HMMEntityMorphEventIds;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.camel.Camel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camel.class)
public class CamelMixin {

    @Inject(
            method = "setDashing",
            at = @At("HEAD")
    )
    public void rgrant$onSetDashingHead(boolean pDashing, CallbackInfo ci) {
        Camel instance = (Camel) (Object) this;
        if (instance.getControllingPassenger() instanceof ServerPlayer player) {
            if (pDashing) {
                AntUtils.addToGenericQuestGoal(player, CamelJumpGoal.class);
            }
        }
    }
}
