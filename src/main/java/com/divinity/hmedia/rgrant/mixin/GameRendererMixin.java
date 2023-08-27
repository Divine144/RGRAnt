package com.divinity.hmedia.rgrant.mixin;

import com.divinity.hmedia.rgrant.cap.AntHolderAttacher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow @Final
    Minecraft minecraft;

    @Redirect(
            method = "renderItemInHand",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;getPlayerMode()Lnet/minecraft/world/level/GameType;"
            )
    )
    public GameType renderItemInHand(MultiPlayerGameMode instance) {
        LocalPlayer player = this.minecraft.player;
        if (player != null) {
            var holder = AntHolderAttacher.getAntHolderUnwrap(player);
            if (holder != null && holder.isMindControlled()) {
                return GameType.SURVIVAL; // Forces hands to render
            }
        }
        return instance.getPlayerMode();
    }
}
