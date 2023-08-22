package com.divinity.hmedia.rgrant.quest.goal;

import dev._100media.hundredmediaquests.goal.BasicQuestGoal;

public class GiveAllayItemGoal extends BasicQuestGoal {
    public GiveAllayItemGoal(double target) {
        super(target);
    }

    @Override
    public String getDescriptionId() {
        return "quest.goal.rgrant.give_allay_item_goal";
    }
}
