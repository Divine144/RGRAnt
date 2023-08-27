package com.divinity.hmedia.rgrant.quest;

import com.divinity.hmedia.rgrant.init.ItemInit;
import com.divinity.hmedia.rgrant.quest.goal.AquireAdvancementGoal;
import com.divinity.hmedia.rgrant.quest.goal.GoatLoseHornGoal;
import com.divinity.hmedia.rgrant.quest.goal.KillEntityCritGoal;
import com.divinity.hmedia.rgrant.quest.goal.TameEntityGoal;
import dev._100media.hundredmediaquests.goal.KillPlayersGoal;
import dev._100media.hundredmediaquests.goal.QuestGoal;
import dev._100media.hundredmediaquests.quest.Quest;
import dev._100media.hundredmediaquests.quest.QuestType;
import dev._100media.hundredmediaquests.reward.ItemQuestReward;
import dev._100media.hundredmediaquests.reward.QuestReward;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class VenomousStingQuest extends Quest {
    public VenomousStingQuest(QuestType<?> type) {
        super(type);
    }

    @Override
    protected List<QuestGoal> initializeGoals() {
        List<QuestGoal> goals = new ArrayList<>();
        goals.add(new AquireAdvancementGoal("lightning_rod_with_villager_no_fire", "quest.goal.rgrant.surge_protector_advancement_goal"));
        goals.add(new GoatLoseHornGoal(3));
        goals.add(new TameEntityGoal(EntityType.CAT, 1));
        goals.add(new KillPlayersGoal(5) {
            @Override
            public boolean tallyKill(Entity entity, DamageSource source) {
                return entity instanceof ServerPlayer player && player.hasEffect(MobEffects.POISON) && super.tallyKill(entity, source);
            }

            @Override
            public String getDescriptionId() {
                return "quest.goal.rgrant.kill_players_poisoned_goal";
            }
        });
        return goals;
    }

    @Override
    protected List<QuestReward> initializeRewards() {
        List<QuestReward> rewards = new ArrayList<>();
        rewards.add(new ItemQuestReward(new ItemStack(ItemInit.VENOMOUS_STING.get())));
        return rewards;
    }
}
