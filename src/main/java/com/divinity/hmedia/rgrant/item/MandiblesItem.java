package com.divinity.hmedia.rgrant.item;

import com.divinity.hmedia.rgrant.init.MarkerInit;
import com.divinity.hmedia.rgrant.init.SoundInit;
import com.mojang.authlib.GameProfile;
import dev._100media.hundredmediaabilities.capability.MarkerHolderAttacher;
import dev._100media.hundredmediageckolib.item.animated.AnimatedItemProperties;
import dev._100media.hundredmediageckolib.item.tool.AnimatedSwordItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ForgeSpawnEggItem;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.function.Consumer;

public class MandiblesItem extends AnimatedSwordItem {
    public MandiblesItem(AnimatedItemProperties properties) {
        super(Tiers.DIAMOND,3, -2.4F, properties);
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity entity, InteractionHand hand) {
        if (entity.level().isClientSide) return InteractionResult.SUCCESS;
        return InteractionResult.CONSUME;
    }

    public static ItemStack getHeadForEntity(Entity entity) {
        if (entity instanceof Skeleton) {
            return Items.SKELETON_SKULL.getDefaultInstance();
        }
        else if (entity instanceof WitherSkeleton) {
            return Items.WITHER_SKELETON_SKULL.getDefaultInstance();
        }
        else if (entity instanceof Zombie) {
            return Items.ZOMBIE_HEAD.getDefaultInstance();
        }
        else if (entity instanceof Creeper) {
            return Items.CREEPER_HEAD.getDefaultInstance();
        }
        else if (entity instanceof Piglin) {
            return Items.PIGLIN_HEAD.getDefaultInstance();
        }
        else if (entity instanceof EnderDragon || entity instanceof EnderDragonPart) {
            return Items.DRAGON_HEAD.getDefaultInstance();
        }
        return ItemStack.EMPTY;
    }
}
