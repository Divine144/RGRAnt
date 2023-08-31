package com.divinity.hmedia.rgrant.datagen;

import com.divinity.hmedia.rgrant.init.*;
import com.google.common.collect.ImmutableMap;
import com.divinity.hmedia.rgrant.RGRAnt;
import dev._100media.hundredmediaabilities.ability.Ability;
import dev._100media.hundredmediaabilities.marker.Marker;
import dev._100media.hundredmediamorphs.morph.Morph;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ModLangProvider extends LanguageProvider {
    protected static final Map<String, String> REPLACE_LIST = ImmutableMap.of(
            "tnt", "TNT",
            "sus", ""
    );

    public ModLangProvider(PackOutput gen) {
        super(gen, RGRAnt.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        EntityInit.ENTITIES.getEntries().forEach(this::entityLang);
        ItemInit.ITEMS.getEntries().forEach(this::itemLang);
        BlockInit.BLOCKS.getEntries().forEach(this::blockLang);
        AbilityInit.ABILITIES.getEntries().forEach(this::abilityLang);
        MarkerInit.MARKERS.getEntries().forEach(this::markerLang);
        MorphInit.MORPHS.getEntries().forEach(this::morphLang);

        add("key.rgrant.skill_tree", "Open Skill Tree");
        add("key.category.rgrant", "RGRAnt");

        add("itemGroup.hundredMediaTab", "100 Media");
        // Quest Descriptions
        add("quest.goal.rgrant.ant_army_kill_enderman_goal.description", "Have \"Ant Army\" Ants kill 5 Endermen");
        add("quest.goal.rgrant.surge_protector_advancement_goal.description", "Earn the Advancement \"Surge Protector\"");
        add("quest.goal.rgrant.make_camel_jump_goal.description", "Make a Camel Jump 10 Times");
        add("quest.goal.rgrant.give_allay_item_goal.description", "Give an Allay Any Item");
        add("quest.goal.rgrant.goat_lose_horn_goal.description", "Make Goats Lose Their Horns 3 Times");
        add("quest.goal.rgrant.hit_villagers_quest_goal.description", "Hit Villagers 10 Times");
        add("quest.goal.rgrant.kill_cave_spider_crit_goal.description", "Kill 15 Cave Spiders with Crits");
        add("quest.goal.rgrant.kill_players_mandibles_goal.description", "Kill 3 Players while they are affected by the Mandibles");
        add("quest.goal.rgrant.kill_players_sting_goal.description", "Kill 7 Players with the Venomous Sting");
        add("quest.goal.rgrant.tame_cat_goal.description", "Tame a Cat");
        add("quest.goal.rgrant.trade_armorer_goal.description", "Trade with a Max Level Armorer");
        add("quest.goal.rgrant.harvest_netherrack_goal.description", "Harvest a Piece Of Netherrack");
        add("quest.goal.rgrant.kill_players_pickaxe_goal.description", "Kill a Player with a Pickaxe");
        add("quest.goal.rgrant.kill_players_poisoned_goal.description", "Kill 5 Players while they are Poisoned");


        // Quest Display Descriptions
        add("quest.goal.rgrant.ant_army_kill_enderman_goal", "Have \"Ant Army\" Ants kill Endermen");
        add("quest.goal.rgrant.surge_protector_advancement_goal", "Earn the Advancement \"Surge Protector\"");
        add("quest.goal.rgrant.make_camel_jump_goal", "Make a Camel Jump 10 Times");
        add("quest.goal.rgrant.give_allay_item_goal", "Give an Allay Any Item");
        add("quest.goal.rgrant.goat_lose_horn_goal", "Make Goats Lose Their Horns");
        add("quest.goal.rgrant.hit_villagers_quest_goal", "Hit Villagers");
        add("quest.goal.rgrant.kill_cave_spider_crit_goal", "Kill Cave Spiders with Crits");
        add("quest.goal.rgrant.kill_players_mandibles_goal", "Kill Players affected by the Mandibles");
        add("quest.goal.rgrant.kill_players_sting_goal", "Kill Players with the Venomous Sting");
        add("quest.goal.rgrant.tame_cat_goal", "Tame a Cat");
        add("quest.goal.rgrant.trade_armorer_goal", "Trade with a Max Level Armorer");
        add("quest.goal.rgrant.harvest_netherrack_goal", "Harvest Netherrack");
        add("quest.goal.rgrant.kill_players_pickaxe_goal", "Kill a Player with a Pickaxe");
        add("quest.goal.rgrant.kill_players_poisoned_goal", "Kill Players while they are Poisoned");
    }

    protected void itemLang(RegistryObject<Item> entry) {
        if (!(entry.get() instanceof BlockItem) || entry.get() instanceof ItemNameBlockItem) {
            addItem(entry, checkReplace(entry));
        }
    }

    protected void blockLang(RegistryObject<Block> entry) {
        addBlock(entry, checkReplace(entry));
    }

    protected void entityLang(RegistryObject<EntityType<?>> entry) {
        addEntityType(entry, checkReplace(entry));
    }

    protected void abilityLang(RegistryObject<Ability> entry) {
        add(entry.get().getDescriptionId(), checkReplace(entry));
    }

    protected void markerLang(RegistryObject<Marker> entry) {
        add(entry.get().getDescriptionId(), checkReplace(entry));
    }

    protected void morphLang(RegistryObject<Morph> entry) {
        add(entry.get().getDescriptionId(), checkReplace(entry));
    }

    protected String checkReplace(RegistryObject<?> registryObject) {
        return Arrays.stream(registryObject.getId().getPath().split("_"))
                .map(this::checkReplace)
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining(" "))
                .trim();
    }

    protected String checkReplace(String string) {
        return REPLACE_LIST.containsKey(string) ? REPLACE_LIST.get(string) : StringUtils.capitalize(string);
    }
}
