package com.divinity.hmedia.rgrant.entity;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class StingerProjectileEntity extends ThrowableProjectile implements GeoEntity {

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public StingerProjectileEntity(EntityType<? extends ThrowableProjectile> pEntityType, Level pLevel) {
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
                if (!living.hasEffect(MobEffects.CONFUSION)) {
                    living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0, false, false, false));
                }
                living.hurt(this.damageSources().mobProjectile(this, getOwner() instanceof LivingEntity l ? l : null), 4);
            }
        }
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }
}
