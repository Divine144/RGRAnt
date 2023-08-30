package com.divinity.hmedia.rgrant.entity;

import com.divinity.hmedia.rgrant.cap.AntHolderAttacher;
import com.divinity.hmedia.rgrant.entity.goal.AttackEntityGoal;
import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
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
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

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
            if (!this.hasEffect(MobEffects.MOVEMENT_SPEED)) {
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 3, false, false, false));
            }
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
    public void aiStep() {
        this.updateSwingTime(); // Makes attack animations sync properly on client
        super.aiStep();
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        float f = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float f1 = (float) this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        if (pEntity instanceof LivingEntity livingEntity) {
            f += EnchantmentHelper.getDamageBonus(this.getMainHandItem(), livingEntity.getMobType());
            f1 += (float)EnchantmentHelper.getKnockbackBonus(this);
        }
        int i = EnchantmentHelper.getFireAspect(this);
        if (i > 0) {
            pEntity.setSecondsOnFire(i * 4);
        }
        Player owner = this.getOwner();
        if (owner != null) {
            boolean flag = pEntity.hurt(this.damageSources().playerAttack(owner), f);
            if (flag) {
                if (f1 > 0.0F && pEntity instanceof LivingEntity livingEntity) {
                    livingEntity.knockback(f1 * 0.5F, Mth.sin(this.getYRot() * ((float) Math.PI / 180F)), -Mth.cos(this.getYRot() * ((float) Math.PI / 180F)));
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                }
                if (pEntity instanceof Player player) {
                    this.maybeDisableShield(player, this.getMainHandItem(), player.isUsingItem() ? player.getUseItem() : ItemStack.EMPTY);
                }
                this.doEnchantDamageEffects(owner, pEntity);
                owner.setLastHurtMob(pEntity);
            }
            return flag;
        }
        return false;
    }

    @Override
    public void swing(InteractionHand pHand) {
        super.swing(pHand);
        Player player = this.getOwner();
        if (player != null) {
            player.swing(pHand); // Imitate swinging like the player is doing it themselves
        }
    }

    @Override
    public void die(DamageSource pDamageSource) {
        if (!this.level().isClientSide) {
            Player player = this.getOwner();
            if (player instanceof ServerPlayer serverPlayer) {
                this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, -1, 0, false, false, false));
                serverPlayer.setGameMode(GameType.SURVIVAL);
                serverPlayer.copyPosition(this);
                AntHolderAttacher.getAntHolder(serverPlayer).ifPresent(h -> h.setMindControlTicks(0));
                serverPlayer.kill();
            }
            this.discard();
        }
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true, p -> MorphHolderAttacher.getCurrentMorph(p).isEmpty()));
        this.goalSelector.addGoal(1, new AttackEntityGoal(this, 1.0F, false));
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

    private void maybeDisableShield(Player pPlayer, ItemStack pMobItemStack, ItemStack pPlayerItemStack) {
        if (!pMobItemStack.isEmpty() && !pPlayerItemStack.isEmpty() && pMobItemStack.getItem() instanceof AxeItem && pPlayerItemStack.is(Items.SHIELD)) {
            float f = 0.25F + (float)EnchantmentHelper.getBlockEfficiency(this) * 0.05F;
            if (this.random.nextFloat() < f) {
                pPlayer.getCooldowns().addCooldown(Items.SHIELD, 100);
                this.level().broadcastEntityEvent(pPlayer, (byte)30);
            }
        }
    }
}
