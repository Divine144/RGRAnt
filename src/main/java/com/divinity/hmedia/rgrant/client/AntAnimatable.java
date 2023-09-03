package com.divinity.hmedia.rgrant.client;

import dev._100media.hundredmediageckolib.client.animatable.MotionAttackAnimatable;
import net.minecraft.client.player.AbstractClientPlayer;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class AntAnimatable extends MotionAttackAnimatable {

    private static final RawAnimation ATTACK = RawAnimation.begin().thenLoop("attack");
    private static final RawAnimation CROUCH = RawAnimation.begin().thenLoop("crouch");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("run");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");

    public AntAnimatable() {}

    @Override
    protected PlayState attackAnimationEvent(AnimationState<? extends MotionAttackAnimatable> state) {
        AnimationController<?> controller = state.getController();
        if (state.getData(DataTickets.ENTITY) instanceof AbstractClientPlayer player) {
            controller.transitionLength(0);
            if (player.swingTime > 0) {
                controller.setAnimation(ATTACK);
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
            if (player.isShiftKeyDown()) {
                controller.setAnimation(CROUCH);
            }
            else if (state.isMoving()) {
                controller.setAnimation(player.isSprinting() && !player.isCrouching() ? RUN : WALK);
            }
            else {
                controller.setAnimation(IDLE);
            }
        }
        return PlayState.CONTINUE;
    }
}
