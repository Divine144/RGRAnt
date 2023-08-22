package com.divinity.hmedia.rgrant.quest.goal;

import com.divinity.hmedia.rgrant.RGRAnt;
import dev._100media.hundredmediaquests.goal.BasicQuestGoal;

public class AquireAdvancementGoal extends BasicQuestGoal {

    private final String advancementID;
    private final String descriptionID;

    public AquireAdvancementGoal(String advancementID, String descriptionID) {
        super(1);
        this.advancementID = advancementID;
        this.descriptionID = "quest.goal." + RGRAnt.MODID + "." + descriptionID;
    }

    public String getAdvancementID() {
        return this.advancementID;
    }

    @Override
    public String getDescriptionId() {
        return descriptionID;
    }
}
