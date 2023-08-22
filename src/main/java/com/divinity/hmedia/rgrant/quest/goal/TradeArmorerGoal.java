package com.divinity.hmedia.rgrant.quest.goal;

import dev._100media.hundredmediaquests.goal.BasicQuestGoal;

public class TradeArmorerGoal extends BasicQuestGoal {
    public TradeArmorerGoal(double target) {
        super(target);
    }

    @Override
    public String getDescriptionId() {
        return "quest.goal.rgrant.trade_armorer_goal";
    }
}
