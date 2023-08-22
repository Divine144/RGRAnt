package com.divinity.hmedia.rgrant.quest.goal;

import dev._100media.hundredmediaquests.goal.BasicQuestGoal;

public class HitVillagersGoal extends BasicQuestGoal {
    public HitVillagersGoal(double target) {
        super(target);
    }

    @Override
    public String getDescriptionId() {
        return "quest.goal.rgrant.hit_villagers_quest_goal";
    }
}
