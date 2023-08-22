package com.divinity.hmedia.rgrant.quest;

import com.divinity.hmedia.rgrant.init.AbilityInit;
import com.divinity.hmedia.rgrant.quest.goal.AntArmyKillEndermanGoal;
import com.divinity.hmedia.rgrant.quest.goal.TradeArmorerGoal;
import com.divinity.hmedia.rgrant.quest.reward.AbilityQuestReward;
import dev._100media.hundredmediaquests.goal.KillPlayersGoal;
import dev._100media.hundredmediaquests.goal.QuestGoal;
import dev._100media.hundredmediaquests.quest.Quest;
import dev._100media.hundredmediaquests.quest.QuestType;
import dev._100media.hundredmediaquests.reward.QuestReward;

import java.util.ArrayList;
import java.util.List;

public class MindControlQuest extends Quest {
    public MindControlQuest(QuestType<?> type) {
        super(type);
    }

    @Override
    protected List<QuestGoal> initializeGoals() {
        List<QuestGoal> goals = new ArrayList<>();
        goals.add(new TradeArmorerGoal(1));
        goals.add(new AntArmyKillEndermanGoal(5));
        goals.add(new KillPlayersGoal(8));
        return goals;
    }

    @Override
    protected List<QuestReward> initializeRewards() {
        List<QuestReward> rewards = new ArrayList<>();
        rewards.add(new AbilityQuestReward(AbilityInit.MIND_CONTROL));
        return rewards;
    }
}
