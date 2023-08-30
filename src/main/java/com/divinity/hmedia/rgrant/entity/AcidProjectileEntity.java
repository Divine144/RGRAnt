package com.divinity.hmedia.rgrant.entity;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AcidProjectileEntity extends ThrowableProjectile implements GeoEntity {

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private final RawAnimation shootAnim = RawAnimation.begin().thenLoop("shoot");

    public AcidProjectileEntity(EntityType<? extends ThrowableProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            if (this.getOwner() != null && this.distanceTo(getOwner()) >= 25) {
                discard();
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        if (!level().isClientSide) {
            discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (pResult.getEntity() != this.getOwner() && pResult.getEntity() instanceof LivingEntity living) {
            if (!level().isClientSide) {
                if (!living.hasEffect(MobEffects.POISON)) {
                    living.addEffect(new MobEffectInstance(MobEffects.POISON, 600, 0, false, false, false));
                }
                if (living.tickCount % 20 == 0) {
                    living.getArmorSlots().forEach(s -> {
                        if (s.getItem() instanceof ArmorItem) {
                            s.hurtAndBreak(50, living, livingEntity -> {});
                        }
                    });
                }
                living.hurt(this.damageSources().mobProjectile(this, getOwner() instanceof LivingEntity l ? l : null), 0);
            }
        }
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller_acid", 0, event -> event.setAndContinue(shootAnim)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}

