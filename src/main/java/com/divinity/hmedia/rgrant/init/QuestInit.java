package com.divinity.hmedia.rgrant.init;

import com.divinity.hmedia.rgrant.RGRAnt;
import com.divinity.hmedia.rgrant.quest.*;
import dev._100media.hundredmediaquests.init.HMQQuestInit;
import dev._100media.hundredmediaquests.quest.QuestType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class QuestInit {
    public static final DeferredRegister<QuestType<?>> QUESTS = DeferredRegister.create(HMQQuestInit.QUESTS.getRegistryKey(), RGRAnt.MODID);

    public static final RegistryObject<QuestType<?>> MANDIBLES = QUESTS.register("mandibles", () -> QuestType.Builder.of(MandiblesQuest::new).repeatable(false).instantTurnIn(false).build());
    public static final RegistryObject<QuestType<?>> ACID_SPRAY = QUESTS.register("acid_spray", () -> QuestType.Builder.of(AcidSprayQuest::new).repeatable(false).instantTurnIn(false).build());
    public static final RegistryObject<QuestType<?>> VENOMOUS_STING = QUESTS.register("venomous_sting", () -> QuestType.Builder.of(VenomousStingQuest::new).repeatable(false).instantTurnIn(false).build());
    public static final RegistryObject<QuestType<?>> ANT_ARMY = QUESTS.register("ant_army", () -> QuestType.Builder.of(AntArmyQuest::new).repeatable(false).instantTurnIn(false).build());
    public static final RegistryObject<QuestType<?>> MIND_CONTROL = QUESTS.register("mind_control", () -> QuestType.Builder.of(MindControlQuest::new).repeatable(false).instantTurnIn(false).build());
}
