package com.divinity.hmedia.rgrant.event;

import com.divinity.hmedia.rgrant.RGRAnt;
import com.divinity.hmedia.rgrant.cap.AntHolderAttacher;
import com.divinity.hmedia.rgrant.entity.AntEntity;
import com.divinity.hmedia.rgrant.init.EffectInit;
import com.divinity.hmedia.rgrant.init.MarkerInit;
import com.divinity.hmedia.rgrant.mixin.EntityAccessor;
import com.divinity.hmedia.rgrant.network.serverbound.EscapeNetPacket;
import com.divinity.hmedia.rgrant.network.NetworkHandler;
import com.divinity.hmedia.rgrant.quest.goal.*;
import com.divinity.hmedia.rgrant.utils.AntUtils;
import dev._100media.hundredmediaabilities.capability.MarkerHolderAttacher;
import dev._100media.hundredmediaquests.cap.QuestHolderAttacher;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.TradeWithVillagerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
        if (event.getSource().getDirectEntity() instanceof AntEntity antEntity) {
            if (!antEntity.level().isClientSide) {
                if (event.getEntity() instanceof EnderMan && antEntity.getOwner() instanceof ServerPlayer player) {
                    AntUtils.addToGenericQuestGoal(player, AntArmyKillEndermanGoal.class);
                }
            }
        }
/*        if (event.getEntity() instanceof MindControlledPlayerEntity mindControlledPlayerEntity) {
            if (!mindControlledPlayerEntity.level().isClientSide) {
                Player player = mindControlledPlayerEntity.getOwner();
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.setGameMode(GameType.SURVIVAL);
                    serverPlayer.copyPosition(mindControlledPlayerEntity);
                    mindControlledPlayerEntity.discard();
                    AntHolderAttacher.getAntHolder(serverPlayer).ifPresent(h -> h.setMindControlTicks(0));
                    serverPlayer.kill();
                }
            }
        }*/
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
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof Camel camel) {
            if (camel.getControllingPassenger() instanceof ServerPlayer player) {
                AntUtils.addToGenericQuestGoal(player, CamelJumpGoal.class);
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
                if (cap.getCaptured() instanceof LivingEntity entity) {
                    Vec3 view = player.getViewVector(1.0f);
                    Vec3 position = player.getEyePosition();
                    Vec3 destination = position.add(view.scale(2.0f));
                    Vec3 lerpDest = vector3dLerp(entity.position(), destination, 0.6f).add(0, -(entity.getEyeHeight() / 2), 0);
                    Vec3 movement = lerpDest.subtract(entity.position());
                    if (entity instanceof EntityAccessor accessor) {
                        movement = accessor.invokeCollide(movement);
                    }
                    entity.moveTo(entity.position().add(movement));
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
            if (holder.getCaptured() instanceof LivingEntity) {
                holder.unCapture();
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        });
    }

    @SubscribeEvent
    public static void netInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.isCanceled()) return;
        if (event.getTarget() instanceof LivingEntity living && living.hasEffect(EffectInit.NETTED.get()) && event.getEntity().isShiftKeyDown()) {
            AntHolderAttacher.getAntHolder(event.getEntity()).ifPresent(holder -> {
                if (holder.getCaptured() instanceof LivingEntity) {
                    holder.unCapture();
                }
                holder.capture(living);
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
        if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof TieredItem && player.hasEffect(EffectInit.NETTED.get())) {
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
