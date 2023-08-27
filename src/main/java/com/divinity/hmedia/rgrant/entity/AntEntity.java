package com.divinity.hmedia.rgrant.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class AntEntity extends PathfinderMob {
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID = SynchedEntityData.defineId(AntEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    public AntEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public boolean isAlliedTo(Entity pEntity) {
        return pEntity instanceof AntEntity || (this.getOwner() != null && pEntity == this.getOwner());
    }

    @Override
    public boolean ignoreExplosion() {
        return true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_OWNER_UUID, Optional.empty());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        if (nbt.contains("Owner"))
            this.entityData.set(DATA_OWNER_UUID, Optional.of(nbt.getUUID("Owner")));
        super.readAdditionalSaveData(nbt);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        if (this.getOwnerUUID() != null)
            nbt.putUUID("Owner", this.getOwnerUUID());
        super.addAdditionalSaveData(nbt);
    }

    @Override
    public void die(DamageSource pDamageSource) {
        if (!this.level().isClientSide) {
            this.level().explode(this.getOwner(), this.getX(), this.getY(), this.getZ(), 2F, Level.ExplosionInteraction.NONE);
        }
        super.die(pDamageSource);
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true) {
            @Override
            public boolean canUse() {
                return super.canUse() && target != AntEntity.this.getOwner();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && target != AntEntity.this.getOwner();
            }
        });
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 0.7F, true));
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, LivingEntity.class, 10F));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.7F));
    }

    @Nullable
    public Player getOwner() {
        try {
            UUID uuid = this.getOwnerUUID();
            return uuid == null ? null : this.level().getPlayerByUUID(uuid);
        }
        catch (Exception e) {
            return null;
        }
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNER_UUID).orElse(null);
    }

    public void setOwnerUUID(@Nullable UUID pUuid) {
        this.entityData.set(DATA_OWNER_UUID, Optional.ofNullable(pUuid));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 2.0D).add(Attributes.ATTACK_DAMAGE, 1D).add(Attributes.MOVEMENT_SPEED, (double)0.3F);
    }
}