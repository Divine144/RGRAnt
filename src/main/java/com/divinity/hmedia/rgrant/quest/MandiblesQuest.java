package com.divinity.hmedia.rgrant.quest;

import com.divinity.hmedia.rgrant.quest.goal.HitVillagersGoal;
import com.divinity.hmedia.rgrant.utils.AntUtils;
import dev._100media.hundredmediaquests.goal.HarvestBlocksGoal;
import dev._100media.hundredmediaquests.goal.KillPlayersGoal;
import dev._100media.hundredmediaquests.goal.QuestGoal;
import dev._100media.hundredmediaquests.quest.Quest;
import dev._100media.hundredmediaquests.quest.QuestType;
import dev._100media.hundredmediaquests.reward.ItemQuestReward;
import dev._100media.hundredmediaquests.reward.QuestReward;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class MandiblesQuest extends Quest {
    public MandiblesQuest(QuestType<?> type) {
        super(type);
    }

    @Override
    protected List<QuestGoal> initializeGoals() {
        List<QuestGoal> goals = new ArrayList<>();
        goals.add(new HitVillagersGoal(10));
        goals.add(new HarvestBlocksGoal(1, Blocks.NETHERRACK) {
            @Override
            public String getDescriptionId() {
                return "quest.goal.rgrant.harvest_netherrack_goal";
            }
        });
        goals.add(new KillPlayersGoal(1) {

            @Override
            public boolean tallyKill(Entity entity, DamageSource source) {
                return source.getEntity() instanceof ServerPlayer player && AntUtils.hasItemEitherHands(player, Items.DIAMOND_PICKAXE) && super.tallyKill(entity, source);
            }

            @Override
            public String getDescriptionId() {
                return "quest.goal.rgrant.kill_players_pickaxe_goal";
            }
        });
        return goals;
    }

    @Override
    protected List<QuestReward> initializeRewards() {
        List<QuestReward> rewards = new ArrayList<>();
        rewards.add(new ItemQuestReward(new ItemStack(ItemInit.MANDIBLES.get())));
        return rewards;
    }
}
