package com.divinity.hmedia.rgrant.quest.goal;

import dev._100media.hundredmediaquests.goal.BasicQuestGoal;

public class KillPlayersVenomousStingGoal extends BasicQuestGoal {
    public KillPlayersVenomousStingGoal(double target) {
        super(target);
    }

    @Override
    public String getDescriptionId() {
        return "quest.goal.rgrant.kill_players_sting_goal";
    }
}
