package com.divinity.hmedia.rgrant.quest.goal;

import dev._100media.hundredmediaquests.goal.BasicQuestGoal;

public class KillPlayersMandiblesGoal extends BasicQuestGoal {
    public KillPlayersMandiblesGoal(double target) {
        super(target);
    }

    @Override
    public String getDescriptionId() {
        return "quest.goal.rgrant.kill_players_mandibles_goal";
    }
}
