package com.divinity.hmedia.rgrant.event;

import com.divinity.hmedia.rgrant.RGRAnt;
import com.divinity.hmedia.rgrant.ability.GigaAntAbility;
import com.divinity.hmedia.rgrant.cap.AntHolder;
import com.divinity.hmedia.rgrant.cap.AntHolderAttacher;
import com.divinity.hmedia.rgrant.entity.AntEntity;
import com.divinity.hmedia.rgrant.init.*;
import com.divinity.hmedia.rgrant.item.MandiblesItem;
import com.divinity.hmedia.rgrant.mixin.EntityAccessor;
import com.divinity.hmedia.rgrant.network.serverbound.EscapeNetPacket;
import com.divinity.hmedia.rgrant.network.NetworkHandler;
import com.divinity.hmedia.rgrant.quest.goal.*;
import com.divinity.hmedia.rgrant.utils.AntUtils;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import dev._100media.hundredmediaabilities.capability.AbilityHolderAttacher;
import dev._100media.hundredmediaabilities.capability.MarkerHolderAttacher;
import dev._100media.hundredmediamorphs.capability.AnimationHolderAttacher;
import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import dev._100media.hundredmediaquests.cap.QuestHolderAttacher;
import dev._100media.hundredmediaquests.goal.KillPlayersGoal;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.List;

@Mod.EventBusSubscriber(modid = RGRAnt.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonForgeEvents {

    @SubscribeEvent
    public static void onAdvancementEarn(AdvancementEvent.AdvancementEarnEvent event) {
        String advancementID = event.getAdvancement().getId().toString();
        QuestHolderAttacher.checkAllGoals(event.getEntity(), goal -> {
            if (goal instanceof AquireAdvancementGoal advancementGoal) {
                if (advancementID.contains(advancementGoal.getAdvancementID())) {
                    advancementGoal.addProgress(1);
                    return true;
                }
            }
            return false;
        });
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal(RGRAnt.MODID)
                .then(Commands.literal("disableMindControl")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> {
                                    var holder = AntHolderAttacher.getAntHolderUnwrap(EntityArgument.getPlayer(context, "player"));
                                    if (holder != null) {
                                        holder.setMindControlTicks(0);
                                    }
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
        );
    }

    @SubscribeEvent
    public static void onPlayerLeave(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            AntHolderAttacher.getAntHolder(player).ifPresent(p -> p.setMindControlTicks(0));
        }
    }

    @SubscribeEvent
    public static void onKill(LivingDeathEvent event) {
        if (event.getSource().getDirectEntity() instanceof ServerPlayer player) {
            if (event.getEntity() instanceof Player other) {
                MarkerHolderAttacher.getMarkerHolder(other).ifPresent(p -> {
                    if (p.hasMarker(MarkerInit.MANDIBLES_MARKER.get())) {
                        AntUtils.addToGenericQuestGoal(player, KillPlayersMandiblesGoal.class);
                    }
                });
            }
        }
        if (event.getSource().getEntity() instanceof ServerPlayer player && event.getSource().is(DamageTypes.PLAYER_EXPLOSION)) {
            AntUtils.addToGenericQuestGoal(player, AntArmyKillEndermanGoal.class);
        }
        else if (event.getSource().getDirectEntity() instanceof AntEntity antEntity) {
            if (!antEntity.level().isClientSide) {
                if (event.getEntity() instanceof EnderMan && antEntity.getOwner() instanceof ServerPlayer player) {
                    AntUtils.addToGenericQuestGoal(player, AntArmyKillEndermanGoal.class);
                }
                else if (event.getEntity() instanceof Player diedPlayer && antEntity.getOwner() instanceof ServerPlayer player) {
                    QuestHolderAttacher.checkAllGoals(player, questGoal -> {
                        if (questGoal instanceof KillPlayersGoal goal) {
                            return goal.tallyKill(diedPlayer, event.getSource());
                        }
                        return false;
                    });
                }
            }
        }
    }

    @SubscribeEvent
    public static void onVillagerTrade(TradeWithVillagerEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (event.getAbstractVillager() instanceof Villager villager) {
                if (villager.getVillagerData().getProfession() == VillagerProfession.ARMORER) {
                    if (villager.getVillagerData().getLevel() == VillagerData.MAX_VILLAGER_LEVEL) {
                        AntUtils.addToGenericQuestGoal(player, TradeArmorerGoal.class);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer entity) {
            var morph = MorphHolderAttacher.getCurrentMorphUnwrap(entity);
            if (morph != null) {
                if (morph == MorphInit.BABY_ANT.get()) {
                    entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, -1, 0, false, false, false));
                }
                else if (morph == MorphInit.BLACK_ANT.get()) {
                    entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 0, false, false, false));
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 0, false, false, false));
                    entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, -1, 1, false, false, false));
                }
                else if (morph == MorphInit.FIRE_ANT.get()) {
                    entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 1, false, false, false));
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, false, false));
                    entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, -1, 1, false, false, false));
                    entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, -1, 0, false, false, false));
                }
                else if (morph == MorphInit.KING_ANT.get()) {
                    entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 2, false, false, false));
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 2, false, false, false));
                    entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, -1, 1, false, false, false));
                    entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, -1, 0, false, false, false));
                    var reachDistance = entity.getAttribute(ForgeMod.BLOCK_REACH.get());
                    var attackDistance = entity.getAttribute(ForgeMod.ENTITY_REACH.get());
                    if (reachDistance != null && attackDistance != null) {
                        reachDistance.setBaseValue(reachDistance.getAttribute().getDefaultValue() + 3);
                        attackDistance.setBaseValue(attackDistance.getAttribute().getDefaultValue() + 3);
                    }
                }
                else if (morph == MorphInit.OMEGA_ANT.get()) {
                    entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 4, false, false, false));
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 4, false, false, false));
                    entity.addEffect(new MobEffectInstance(MobEffects.JUMP, -1, 3, false, false, false));
                    entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, -1, 1, false, false, false));
                    entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, -1, 0, false, false, false));
                    var reachDistance = entity.getAttribute(ForgeMod.BLOCK_REACH.get());
                    var attackDistance = entity.getAttribute(ForgeMod.ENTITY_REACH.get());
                    if (reachDistance != null && attackDistance != null) {
                        reachDistance.setBaseValue(reachDistance.getAttribute().getDefaultValue() + 5);
                        attackDistance.setBaseValue(attackDistance.getAttribute().getDefaultValue() + 5);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (event.getSource().getDirectEntity() instanceof ServerPlayer player) {
            if (event.getEntity() instanceof Villager) {
                AntUtils.addToGenericQuestGoal(player, HitVillagersGoal.class);
            }
        }
        if (event.getEntity() instanceof ServerPlayer player) {
            AntHolderAttacher.getAntHolder(player).ifPresent(cap -> {
                if (cap.getRemainingShield() > 0) {

                    player.level().playSound(null, player.blockPosition(), SoundInit.SWARM_SHIELD.get(), SoundSource.PLAYERS, 0.5f, 1f);

                    cap.setRemainingShield(cap.getRemainingShield() - event.getAmount());

                    if (cap.getRemainingShield() <= 0) {
                        AbilityHolderAttacher.getAbilityHolder(player).ifPresent(abilityHolder -> abilityHolder.addCooldown(AbilityInit.SWARM_SHIELD.get(), true));
                    }
                    event.setCanceled(true);
                }
            });

        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player instanceof ServerPlayer player && event.phase == TickEvent.Phase.END) {
            MarkerHolderAttacher.getMarkerHolder(player).ifPresent(p -> {
                if (p.hasMarker(MarkerInit.MANDIBLES_MARKER.get())) {
                    if (!player.hasEffect(MobEffects.BLINDNESS)) {
                        p.removeMarker(MarkerInit.MANDIBLES_MARKER.get(), false);
                    }
                }
            });
            AntHolderAttacher.getAntHolder(player).ifPresent(cap -> {
                if (player.hasEffect(EffectInit.NETTED.get())) {
                    int toSwing = cap.getToSwing();
                    if (toSwing > 0) {
                        String s;
                        if (toSwing == 1) {
                            s = "Cut your way out " + toSwing + " times to escape!";
                        } else {
                            s = "Cut your way out to escape!";
                        }
                        player.displayClientMessage(Component.literal(s).withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), true);
                    }
                }
                if (cap.getGigaAntTicks() > 0) {
                    cap.setGigaAntTicks(cap.getGigaAntTicks() - 1);
                    List<Player> targets = player.level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(15), e -> e != player && MorphHolderAttacher.getCurrentMorph(e).map(m -> !m.getDescriptionId().contains("phoenix")).orElse(true) && e.distanceToSqr(player) <= 10 * 10);
                    if (!targets.isEmpty()) {
                        Player target = targets.get(0);
                        player.attack(target);
                        player.lookAt(EntityAnchorArgument.Anchor.EYES, target.position());
                    }
                    player.serverLevel()
                            .players()
                            .stream()
                            .filter(p -> MorphHolderAttacher.getCurrentMorph(p).isEmpty() && !p.hasEffect(MobEffects.GLOWING))
                            .forEach(p -> {
                                p.addEffect(new MobEffectInstance(MobEffects.GLOWING, -1, 0, false,false,false));
                            });
                    if (cap.getGigaAntTicks() <= 0) {
                        AbilityInit.GIGA_ANT.get().executePressed(player.serverLevel(), player);
                    }
                }
                if (cap.getCaptured() instanceof LivingEntity entity) {
                    if (!entity.hasEffect(EffectInit.NETTED.get())) {
                        cap.unCapture();
                        return;
                    }
                    Vec3 view = player.getViewVector(1.0f);
                    Vec3 position = player.getEyePosition();
                    Vec3 destination = position.add(view.scale(2f));
                    Vec3 lerpDest = vector3dLerp(entity.position(), destination, 0.6f).add(0, -(entity.getEyeHeight() / 2), 0);
                    Vec3 movement = lerpDest.subtract(entity.position());
                    if (entity instanceof EntityAccessor accessor) {
                        movement = accessor.invokeCollide(movement);
                    }
                    var pos = entity.position().add(movement);
                    entity.teleportTo(pos.x, pos.y, pos.z);
                    entity.hurtMarked = true;
                    entity.fallDistance = 0;
                }
                else {
                    cap.unCapture();
                }
            });
        }
    }

    @SubscribeEvent
    public static void playerRightClick(PlayerInteractEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (event.isCanceled()) return;
        if (event instanceof PlayerInteractEvent.LeftClickBlock || event instanceof PlayerInteractEvent.LeftClickEmpty) return;
        if (event.getEntity().isShiftKeyDown()) return;
        AntHolderAttacher.getAntHolder(event.getEntity()).ifPresent(holder -> {
            if (holder.getCaptured() instanceof LivingEntity living) {
                holder.unCapture();
                holder.capture(living);
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        });
    }

    @SubscribeEvent
    public static void netInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.isCanceled()) return;
        if (event.getEntity() instanceof ServerPlayer player) {
            ItemStack stack = ItemStack.EMPTY;
            if (AntUtils.hasItemEitherHands(player, ItemInit.MANDIBLES.get())) {
                for (InteractionHand hand : InteractionHand.values()) {
                    if (player.getItemInHand(hand).is(ItemInit.MANDIBLES.get())) {
                        stack = player.getItemInHand(hand);
                        break;
                    }
                }
                if (event.getTarget() instanceof LivingEntity entity) {
                    ItemStack itemStack = MandiblesItem.getHeadForEntity(entity);
                    CompoundTag tag = stack.getOrCreateTag();
                    if (tag.getInt(String.valueOf(entity.getId())) != entity.getId()) {
                        if (!itemStack.isEmpty()) {
                            player.getInventory().add(itemStack);
                        }
                        else if (entity instanceof Player otherPlayer) {
                            ItemStack playerHeadStack = new ItemStack(Items.PLAYER_HEAD);
                            GameProfile gameprofile = otherPlayer.getGameProfile();
                            playerHeadStack.getOrCreateTag().put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), gameprofile));
                            player.getInventory().add(playerHeadStack);
                            MarkerHolderAttacher.getMarkerHolder(otherPlayer).ifPresent(h -> h.addMarker(MarkerInit.MANDIBLES_MARKER.get(), false));
                        }
                        else {
                            ItemStack pickedStack = entity.getPickedResult(null);
                            if (pickedStack != null && (pickedStack.getItem() instanceof SpawnEggItem)) {
                                player.getInventory().add(new ItemStack(Items.PLAYER_HEAD));
                            }
                        }
                        entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 0));
                        player.level().playSound(null, player.blockPosition(), SoundInit.MANDIBLES.get(), SoundSource.PLAYERS, 0.5f, 1f);
                        tag.putInt(String.valueOf(entity.getId()), entity.getId());
                    }
                }
                else if (event.getTarget() instanceof EnderDragonPart part) {
                    var entity = part.getParent();
                    CompoundTag tag = stack.getOrCreateTag();
                    if (tag.getInt(String.valueOf(entity.getId())) != entity.getId()) {
                        if (!player.getInventory().contains(Items.DRAGON_HEAD.getDefaultInstance())) {
                            player.getInventory().add(new ItemStack(Items.DRAGON_HEAD));
                        }
                        tag.putInt(String.valueOf(entity.getId()), entity.getId());
                    }
                    entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 0));
                    player.level().playSound(null, player.blockPosition(), SoundInit.MANDIBLES.get(), SoundSource.PLAYERS, 0.5f, 1f);
                }
            }
        }
        if (event.getTarget() instanceof LivingEntity living && living.hasEffect(EffectInit.NETTED.get()) && event.getEntity().isShiftKeyDown()) {
            AntHolderAttacher.getAntHolder(event.getEntity()).ifPresent(holder -> {
                if (holder.getCaptured() instanceof LivingEntity) {
                    holder.unCapture();
                }
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            });
        }
    }

    @SubscribeEvent
    public static void playerLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getEntity().level().isClientSide) return;
        if (event.getHand() == InteractionHand.MAIN_HAND) {
            AntUtils.scanHitWithFollowup(event.getEntity(), event.getEntity().getAttributeValue(ForgeMod.BLOCK_REACH.get()), false, result -> {
                if (result instanceof BlockHitResult hitResult && hitResult.getDirection() == event.getFace()) {
                    playerLeftClicked(event.getEntity());
                }
            });
        }
    }

    @SubscribeEvent
    public static void playerLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        if (!event.getEntity().level().isClientSide) return;
        playerLeftClicked(event.getEntity());
    }

    @SubscribeEvent
    public static void playerAttackEntity(AttackEntityEvent event) {
        if (event.getEntity().level().isClientSide) return;
        playerLeftClicked(event.getEntity());
    }

    public static void playerLeftClicked(Player player) {
        if (player.hasEffect(EffectInit.NETTED.get())) {
            if (player.level().isClientSide) {
                NetworkHandler.INSTANCE.sendToServer(new EscapeNetPacket());
            }
            else {
                AntHolderAttacher.getAntHolder(player).ifPresent(mechaSharkHolder -> {
                    mechaSharkHolder.decrementToSwing();
                    if (mechaSharkHolder.getToSwing() <= 0) {
                        player.removeEffect(EffectInit.NETTED.get());
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public static void onTame(AnimalTameEvent event) {
        if (event.getTamer() instanceof ServerPlayer player) {
            QuestHolderAttacher.checkAllGoals(player, goal -> {
                if (goal instanceof TameEntityGoal tameEntityGoal) {
                    return tameEntityGoal.mobsTamed(event.getAnimal().getType());
                }
                return false;
            });
        }
    }

    public static Vec3 vector3dLerp(Vec3 start, Vec3 end, double s) {
        return start.scale(1.0 - s).add(end.scale(s));
    }
}
