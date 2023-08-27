package com.divinity.hmedia.rgrant.entity;

import com.divinity.hmedia.rgrant.cap.AntHolderAttacher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

// TODO: Implement Kill Aura and Speed
public class MindControlledPlayerEntity extends PathfinderMob {
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID = SynchedEntityData.defineId(MindControlledPlayerEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    public MindControlledPlayerEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
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
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            Player player = this.getOwner();
            if (player == null) discard();
            else {
                AntHolderAttacher.getAntHolder(player).ifPresent(c -> {
                    if (c.isMindControlled()) {
                        c.decreaseMindControlTicks();
                        if (player instanceof ServerPlayer serverPlayer) {
                            serverPlayer.setGameMode(GameType.SPECTATOR);
                            if (serverPlayer.getCamera() != this) {
                                serverPlayer.setCamera(this);
                            }
                        }
                    }
                    else {
                        if (player instanceof ServerPlayer serverPlayer) {
                            serverPlayer.setGameMode(GameType.SURVIVAL);
                            serverPlayer.copyPosition(this);
                        }
                        discard();
                    }
                });
            }
        }
    }

    @Override
    public void swing(InteractionHand pHand) {
        super.swing(pHand);
        Player player = this.getOwner();
        if (player != null) {
            player.swing(pHand); // Imitate swinging like the player is doing it themselves (idk if this will work if player is spectating an entity)
        }
    }

    @Override
    public void die(DamageSource pDamageSource) {
        if (!this.level().isClientSide) {
            Player player = this.getOwner();
            if (player instanceof ServerPlayer serverPlayer) {
                this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, -1, 0));
                serverPlayer.setGameMode(GameType.SURVIVAL);
                serverPlayer.copyPosition(this);
                AntHolderAttacher.getAntHolder(serverPlayer).ifPresent(h -> h.setMindControlTicks(0));
                serverPlayer.kill();
            }
            this.discard();
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        Player player = this.getOwner();
        if (player != null) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                this.setItemSlot(slot, player.getItemBySlot(slot));
            }
//            this.setItemSlot(EquipmentSlot.HEAD, ); TODO: Set head to mind control armor
        }
        return pSpawnData;
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, false));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0F, true));
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
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.ATTACK_DAMAGE, 2D).add(Attributes.MOVEMENT_SPEED, (double)0.3F);
    }
}
