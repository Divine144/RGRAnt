package com.divinity.hmedia.rgrant.mixin;

import com.divinity.hmedia.rgrant.quest.goal.CamelJumpGoal;
import com.divinity.hmedia.rgrant.utils.AntUtils;
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
            at = @At("HEAD"),
            cancellable = true
    )
    public void setDashing(boolean pDashing, CallbackInfo ci) {
        Camel instance = (Camel) (Object) this;
        if (instance.getControllingPassenger() instanceof ServerPlayer player) {
            AntUtils.addToGenericQuestGoal(player, CamelJumpGoal.class);
        }
    }
}
