package com.divinity.hmedia.rgrant.skill;

import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import dev._100media.hundredmediamorphs.morph.Morph;
import dev._100media.hundredmediaquests.skill.Skill;
import dev._100media.hundredmediaquests.skill.defaults.SimpleSkill;
import dev._100media.hundredmediaquests.skill.requirements.SkillRequirement;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MorphSkill extends SimpleSkill {
    private final Supplier<Morph> morphSupplier;

    public MorphSkill(@NotNull MutableComponent name, @Nullable MutableComponent description, @NotNull List<SkillRequirement> requirements, Supplier<Morph> morphSupplier, @Nullable Consumer<ServerPlayer> onSkillAdd, @Nullable Consumer<ServerPlayer> onSkillRemove) {
        super(name, description, requirements, onSkillAdd, onSkillRemove, null);
        this.morphSupplier = morphSupplier;
    }

    public MorphSkill(@NotNull MutableComponent name, @Nullable MutableComponent description, @NotNull List<SkillRequirement> requirements, Supplier<Morph> morphSupplier) {
        super(name, description, requirements, p -> {}, p -> {}, null);
        this.morphSupplier = morphSupplier;
    }

    @Override
    protected void onSkillAdd(@NotNull ServerPlayer serverPlayer) {
        MorphHolderAttacher.getMorphHolder(serverPlayer).ifPresent(cap -> cap.setCurrentMorph(morphSupplier.get(), true));
        super.onSkillAdd(serverPlayer);
    }

    @Override
    protected void onSkillRemove(@NotNull ServerPlayer serverPlayer) {
        MorphHolderAttacher.getMorphHolder(serverPlayer).ifPresent(cap -> cap.setCurrentMorph(null, true));
        super.onSkillRemove(serverPlayer);
    }
}

