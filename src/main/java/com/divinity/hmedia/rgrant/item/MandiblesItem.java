package com.divinity.hmedia.rgrant.item;

import com.divinity.hmedia.rgrant.init.MarkerInit;
import com.mojang.authlib.GameProfile;
import dev._100media.hundredmediaabilities.capability.MarkerHolderAttacher;
import dev._100media.hundredmediageckolib.item.animated.AnimatedItemProperties;
import dev._100media.hundredmediageckolib.item.tool.AnimatedSwordItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
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
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.function.Consumer;

public class MandiblesItem extends AnimatedSwordItem {
    public MandiblesItem(AnimatedItemProperties properties) {
        super(Tiers.DIAMOND,3, -2.4F, properties);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
/*        consumer.accept(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null)
                    this.renderer = new GeoItemRenderer<>(new DefaultedItemGeoModel<>(new ResourceLocation(RGRAnt.MODID, "mandibles")));

                return this.renderer;
            }
        });*/
    }

    @Override
    public @NotNull InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity entity, InteractionHand hand) {
        if (entity.level().isClientSide) return InteractionResult.SUCCESS;
        ItemStack itemStack = getHeadForEntity(entity);
        if (!itemStack.isEmpty()) {
            playerIn.getInventory().add(itemStack);
            return InteractionResult.SUCCESS;
        }
        else if (entity instanceof Player player) {
            ItemStack playerHeadStack = new ItemStack(Items.PLAYER_HEAD);
            GameProfile gameprofile = player.getGameProfile();
            playerHeadStack.getOrCreateTag().put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), gameprofile));
            playerIn.getInventory().add(playerHeadStack);
            MarkerHolderAttacher.getMarkerHolder(player).ifPresent(h -> h.addMarker(MarkerInit.MANDIBLES_MARKER.get(), false));
            return InteractionResult.SUCCESS;
        }
        else playerIn.getInventory().add(new ItemStack(Items.PLAYER_HEAD));
        entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 0));
        return InteractionResult.CONSUME;
    }

    private ItemStack getHeadForEntity(Entity entity) {
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