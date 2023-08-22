package com.divinity.hmedia.rgrant.quest.goal;

import dev._100media.hundredmediaquests.goal.BasicQuestGoal;

public class CamelJumpGoal extends BasicQuestGoal {
    public CamelJumpGoal(double target) {
        super(target);
    }

    @Override
    public String getDescriptionId() {
        return "quest.goal.rgrant.make_camel_jump_goal";
    }
}