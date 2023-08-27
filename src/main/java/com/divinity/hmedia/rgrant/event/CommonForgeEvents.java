package com.divinity.hmedia.rgrant.event;

import com.divinity.hmedia.rgrant.RGRAnt;
import com.divinity.hmedia.rgrant.cap.AntHolderAttacher;
import com.divinity.hmedia.rgrant.entity.AntEntity;
import com.divinity.hmedia.rgrant.entity.MindControlledPlayerEntity;
import com.divinity.hmedia.rgrant.init.MarkerInit;
import com.divinity.hmedia.rgrant.quest.goal.*;
import com.divinity.hmedia.rgrant.utils.AntUtils;
import dev._100media.hundredmediaabilities.capability.MarkerHolderAttacher;
import dev._100media.hundredmediaquests.cap.QuestHolderAttacher;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
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
}
