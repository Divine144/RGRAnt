package com.divinity.hmedia.rgrant.quest.goal;

import dev._100media.hundredmediaquests.goal.BasicQuestGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

public class TameEntityGoal extends BasicQuestGoal {
    private EntityType<?> type;

    public TameEntityGoal(EntityType<?> type, double target) {
        super(target);
        this.type = type;
    }

    public boolean mobsTamed(EntityType<?> type) {
        if (this.type == type) {
            return addProgress(1);
        }
        return false;
    }

    @Override
    public String getDescriptionId() {
        return "quest.goal.rgrant.tame_cat_goal";
    }
    @Override
    public CompoundTag save() {
        CompoundTag tag = super.save();
        tag.putString("entity", ForgeRegistries.ENTITY_TYPES.getKey(type).toString());
        return tag;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.type = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(tag.getString("entity")));
    }
}
