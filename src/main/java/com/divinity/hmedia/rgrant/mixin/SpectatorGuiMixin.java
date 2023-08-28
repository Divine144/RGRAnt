package com.divinity.hmedia.rgrant.mixin;

import com.divinity.hmedia.rgrant.cap.AntHolderAttacher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(SpectatorGui.class)
public class SpectatorGuiMixin {

    @Shadow @Nullable private SpectatorMenu menu;

    @Inject(method = "onHotbarSelected", at = @At("TAIL"))
    public void onHotBarSelected(int pSlot, CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            var holder = AntHolderAttacher.getAntHolderUnwrap(player);
            if (holder != null && holder.isMindControlled()) {
                this.menu = null;
            }
        }
    }

    @Inject(method = "onMouseMiddleClick", at = @At("TAIL"))
    public void onMouseMiddleClick(CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            var holder = AntHolderAttacher.getAntHolderUnwrap(player);
            if (holder != null && holder.isMindControlled()) {
                this.menu = null;
            }
        }
    }
}
