package com.divinity.hmedia.rgrant.entity;

import com.divinity.hmedia.rgrant.init.ItemInit;
import com.divinity.hmedia.rgrant.init.SoundInit;
import com.divinity.hmedia.rgrant.utils.AntUtils;
import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AntDroneEntity extends PathfinderMob implements GeoEntity {

    private static final EntityDataAccessor<Boolean> IS_ATTACK = SynchedEntityData.defineId(AntDroneEntity.class, EntityDataSerializers.BOOLEAN);
    private static final RawAnimation ATTACK = RawAnimation.begin().thenLoop("attack");
    private static final RawAnimation CROUCH = RawAnimation.begin().thenLoop("crouch");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("run");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public AntDroneEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ServerLevel level) {
            if (tickCount % 20 == 0) {
                level.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getEyeY(), this.getZ(), 4, 0, 0, 0, 0.1f);
                var list = AntUtils.getEntitiesInRange(this, Player.class, 10, 10, 10, p -> MorphHolderAttacher.getCurrentMorph(p).isPresent());
                if (!list.isEmpty()) {
                    Player player = list.get(0);
                    if (isAttack()) {
                        player.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0));
                        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
                        player.level().playSound(null, player.blockPosition(), SoundInit.BUG_SPRAY.get(), SoundSource.PLAYERS, 0.5f, 1f);
                    }
                    else {
                        player.level().playSound(null, player.blockPosition(), SoundInit.ANT_DRONE_WARNING.get(), SoundSource.PLAYERS, 0.5f, 1f);
                    }
                }
            }
        }
    }

    @Override
    public void aiStep() {
        this.updateSwingTime(); // Makes attack animations sync properly on client
        super.aiStep();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(IS_ATTACK, false);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        this.entityData.set(IS_ATTACK, nbt.getBoolean("isAttack"));
        super.readAdditionalSaveData(nbt);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        nbt.putBoolean("isAttack", entityData.get(IS_ATTACK));
        super.addAdditionalSaveData(nbt);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, LivingEntity.class, 10F));
    }

    @Override
    protected InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (!this.level().isClientSide) {
            if (!pPlayer.isShiftKeyDown()) {
                boolean isAttack = !entityData.get(IS_ATTACK);
                pPlayer.displayClientMessage(Component.literal("%s mode activated.".formatted(isAttack ? "Attack" : "Defense"))
                        .withStyle(ChatFormatting.RED), true);
                entityData.set(IS_ATTACK, isAttack);
            }
            else {
                pPlayer.getInventory().add(ItemInit.ANT_DRONE.get().getDefaultInstance());
                discard();
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, state -> {
            AnimationController<?> controller = state.getController();
            if (state.getData(DataTickets.ENTITY) instanceof AntDroneEntity entity) {
                controller.transitionLength(0);
                if (entity.swingTime > 0) {
                    controller.setAnimation(ATTACK);
                    return PlayState.CONTINUE;
                }
                else if (state.isMoving()) {
                    controller.setAnimation(entity.isSprinting() && !entity.isCrouching() ? RUN : WALK);
                }
                else {
                    controller.setAnimation(IDLE);
                }
            }
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    public boolean isAttack() {
        return entityData.get(IS_ATTACK);
    }

    public void setIsAttack(boolean isAttack) {
        this.entityData.set(IS_ATTACK, isAttack);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.ATTACK_DAMAGE, 2D).add(Attributes.MOVEMENT_SPEED, (double)0.3F);
    }
}
