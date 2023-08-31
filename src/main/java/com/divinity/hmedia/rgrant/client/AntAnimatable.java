package com.divinity.hmedia.rgrant.client;

import dev._100media.hundredmediageckolib.client.animatable.MotionAttackAnimatable;
import net.minecraft.client.player.AbstractClientPlayer;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class AntAnimatable extends MotionAttackAnimatable {

    private String attackName = "";
    private String vehicleSitName = "";
    private String crouchName = "";
    private String walkName = "";
    private String runName = "";
    private String idleName = "";

    public AntAnimatable() {}

    public AntAnimatable(String attackName, String vehicleSitName, String crouchName, String walkName, String runName, String idleName) {
        this.attackName = attackName;
        this.vehicleSitName = vehicleSitName;
        this.crouchName = crouchName;
        this.walkName = walkName;
        this.runName = runName;
        this.idleName = idleName;
    }

    @Override
    protected PlayState attackAnimationEvent(AnimationState<? extends MotionAttackAnimatable> state) {
        AnimationController<?> controller = state.getController();
        if (state.getData(DataTickets.ENTITY) instanceof AbstractClientPlayer player) {
            controller.transitionLength(0);
            if (player.swingTime > 0) {
                controller.setAnimation(RawAnimation.begin().thenLoop(this.getAnimationName(attackName, "attack")));
                return PlayState.CONTINUE;
            }
            motionAnimationEvent(state);
        }
        return PlayState.CONTINUE;
    }

    @Override
    protected PlayState motionAnimationEvent(AnimationState<? extends MotionAttackAnimatable> state) {
        AnimationController<?> controller = state.getController();
        if (state.getData(DataTickets.ENTITY) instanceof AbstractClientPlayer player) {
            controller.transitionLength(0);
            if (player.getVehicle() != null) {
                controller.setAnimation(RawAnimation.begin().thenLoop(this.getAnimationName(vehicleSitName, "Pose")));
            }
            else if (player.isShiftKeyDown()) {
                controller.setAnimation(RawAnimation.begin().thenLoop(this.getAnimationName(crouchName, "crouch")));
            }
            else if (state.isMoving()) {
                controller.setAnimation(RawAnimation.begin().thenLoop(
                        player.isSprinting() && !player.isCrouching()
                        ? getAnimationName(runName, "run")
                        : getAnimationName(walkName, "walk")
                ));
            }
            else {
                controller.setAnimation(RawAnimation.begin().thenLoop(this.getAnimationName(idleName, "idle")));
            }
        }
        return PlayState.CONTINUE;
    }

    private String getAnimationName(String animationName, String defaultName) {
        return animationName.isEmpty() ? defaultName : animationName;
    }
}
