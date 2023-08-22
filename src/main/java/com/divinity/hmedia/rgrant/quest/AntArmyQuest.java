package com.divinity.hmedia.rgrant.quest;

import com.divinity.hmedia.rgrant.init.AbilityInit;
import com.divinity.hmedia.rgrant.quest.goal.CamelJumpGoal;
import com.divinity.hmedia.rgrant.quest.goal.GiveAllayItemGoal;
import com.divinity.hmedia.rgrant.quest.goal.KillPlayersVenomousStingGoal;
import com.divinity.hmedia.rgrant.quest.reward.AbilityQuestReward;
import dev._100media.hundredmediaquests.goal.QuestGoal;
import dev._100media.hundredmediaquests.quest.Quest;
import dev._100media.hundredmediaquests.quest.QuestType;
import dev._100media.hundredmediaquests.reward.QuestReward;

import java.util.ArrayList;
import java.util.List;

public class AntArmyQuest extends Quest {
    public AntArmyQuest(QuestType<?> type) {
        super(type);
    }

    @Override
    protected List<QuestGoal> initializeGoals() {
        List<QuestGoal> goals = new ArrayList<>();
        goals.add(new GiveAllayItemGoal(1));
        goals.add(new CamelJumpGoal(10));
        goals.add(new KillPlayersVenomousStingGoal(7));
        return goals;
    }

    @Override
    protected List<QuestReward> initializeRewards() {
        List<QuestReward> rewards = new ArrayList<>();
        rewards.add(new AbilityQuestReward(AbilityInit.ANT_ARMY));
        return rewards;
    }
}
