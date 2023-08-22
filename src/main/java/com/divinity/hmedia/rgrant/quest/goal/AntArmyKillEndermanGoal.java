package com.divinity.hmedia.rgrant.quest.goal;

import dev._100media.hundredmediaquests.goal.BasicQuestGoal;

public class AntArmyKillEndermanGoal extends BasicQuestGoal {
    public AntArmyKillEndermanGoal(double target) {
        super(target);
    }

    @Override
    public String getDescriptionId() {
        return "quest.goal.rgrant.ant_army_kill_enderman_goal";
    }
}
