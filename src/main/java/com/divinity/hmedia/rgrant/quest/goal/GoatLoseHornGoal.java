package com.divinity.hmedia.rgrant.quest.goal;

import dev._100media.hundredmediaquests.goal.BasicQuestGoal;

public class GoatLoseHornGoal extends BasicQuestGoal {
    public GoatLoseHornGoal(double target) {
        super(target);
    }

    @Override
    public String getDescriptionId() {
        return "quest.goal.rgrant.goat_lose_horn_goal";
    }
}

