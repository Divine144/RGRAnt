package com.divinity.hmedia.rgrant.init;

import com.divinity.hmedia.rgrant.RGRAnt;
import com.divinity.hmedia.rgrant.requirement.ItemTreasureMapSkillRequirement;
import com.divinity.hmedia.rgrant.skill.MorphSkill;
import com.divinity.hmedia.rgrant.skill.tree.CombatTree;
import com.divinity.hmedia.rgrant.skill.tree.EvolutionTree;
import com.divinity.hmedia.rgrant.skill.tree.UtilityTree;
import dev._100media.hundredmediaabilities.ability.Ability;
import dev._100media.hundredmediaabilities.capability.AbilityHolderAttacher;
import dev._100media.hundredmediaabilities.init.HMAAbilityInit;
import dev._100media.hundredmediaquests.init.HMQSkillsInit;
import dev._100media.hundredmediaquests.skill.Skill;
import dev._100media.hundredmediaquests.skill.SkillTree;
import dev._100media.hundredmediaquests.skill.defaults.MenuProvidingTree;
import dev._100media.hundredmediaquests.skill.defaults.QuestSkill;
import dev._100media.hundredmediaquests.skill.defaults.SimpleSkill;
import dev._100media.hundredmediaquests.skill.requirements.ItemSkillRequirement;
import dev._100media.hundredmediaquests.skill.requirements.ItemTagSkillRequirement;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Arrays;
import java.util.function.Consumer;

public class SkillInit {
    public static final DeferredRegister<SkillTree> SKILL_TREES = DeferredRegister.create(HMQSkillsInit.SKILL_TREES.getRegistryName(), RGRAnt.MODID);
    public static final DeferredRegister<Skill> SKILLS = DeferredRegister.create(HMQSkillsInit.SKILLS.getRegistryName(), RGRAnt.MODID);

    // Evolution
    public static final RegistryObject<Skill> BABY_ANT = SKILLS.register("baby_ant", () -> new MorphSkill(
            Component.literal("Baby Ant"),
            Component.literal("%s Hearts, Haste %s".formatted(5, "I")),
            Arrays.asList(),
            MorphInit.BABY_ANT
    ));
    public static final RegistryObject<Skill> BLACK_ANT = SKILLS.register("black_ant", () -> new MorphSkill(
            Component.literal("Black Ant"),
            Component.literal("%s Hearts, Strength %s, Speed %s, Haste %s".formatted(10, "I", "I", "II")),
            Arrays.asList(
                    new ItemSkillRequirement(() -> Items.ROTTEN_FLESH, 32),
                    new ItemSkillRequirement(() -> Items.LAPIS_BLOCK, 3),
                    new ItemSkillRequirement(() -> Items.NAME_TAG, 1)
            ),
            MorphInit.BLACK_ANT,
            player -> {
                unlockAbility(player, AbilityInit.SIZE_UP.get());
                unlockAbility(player, AbilityInit.SIZE_DOWN.get());
            },
            player -> {
                removeAbility(player, AbilityInit.SIZE_UP.get());
                removeAbility(player, AbilityInit.SIZE_DOWN.get());
            }
    ));
    public static final RegistryObject<Skill> FIRE_ANT = SKILLS.register("fire_ant", () -> new MorphSkill(
            Component.literal("Fire Ant"),
            Component.literal("%s Hearts, Strength %s, Speed %s, Haste %s, Fire Immunity".formatted(20, "II", "II", "II")),
            Arrays.asList(
                    new ItemSkillRequirement(() -> Items.SPONGE, 16),
                    new ItemSkillRequirement(() -> Items.NETHER_WART, 16),
                    new ItemSkillRequirement(() -> Items.RECOVERY_COMPASS, 1)
            ),
            MorphInit.FIRE_ANT
    ));
    public static final RegistryObject<Skill> KING_ANT = SKILLS.register("king_ant", () -> new MorphSkill(
            Component.literal("King Ant"),
            Component.literal("%s Hearts, Strength %s, Speed %s, Haste %s, Fire Immunity, +%s Block Reach".formatted(30, "III", "III", "II", 3)),
            Arrays.asList(
                    new ItemSkillRequirement(() -> Items.NAUTILUS_SHELL, 1),
                    new ItemSkillRequirement(() -> Items.BLAZE_ROD, 12),
                    new ItemSkillRequirement(() -> Items.SKELETON_SKULL, 1),
                    new ItemSkillRequirement(() -> Items.WITHER_SKELETON_SKULL, 1),
                    new ItemSkillRequirement(() -> Items.CREEPER_HEAD, 1),
                    new ItemSkillRequirement(() -> Items.ZOMBIE_HEAD, 1),
                    new ItemSkillRequirement(() -> Items.PIGLIN_HEAD, 1),
                    new ItemSkillRequirement(() -> Items.PLAYER_HEAD, 1) // Might not work
            ),
            MorphInit.KING_ANT
    ));
    public static final RegistryObject<Skill> OMEGA_ANT = SKILLS.register("omega_ant", () -> new MorphSkill(
            Component.literal("Omega Velvet Ant"),
            Component.literal("%s Hearts, Strength %s, Speed %s, Jump Boost %s, Haste %s, Fire Immunity, +%s Block Reach".formatted(50, "V", "V", "IV", "II", 5)),
            Arrays.asList(
                    new ItemSkillRequirement(() -> Items.ELYTRA, 1),
                    new ItemSkillRequirement(() -> Items.POPPED_CHORUS_FRUIT, 32),
                    new ItemSkillRequirement(() -> Items.TOTEM_OF_UNDYING, 3)
            ),
            MorphInit.OMEGA_ANT
    ));

    public static final RegistryObject<MenuProvidingTree> EVOLUTION_TREE = SKILL_TREES.register("evolution", () -> new EvolutionTree(
            Component.literal("Evolution"),
            Arrays.asList(BABY_ANT, BLACK_ANT, FIRE_ANT, KING_ANT, OMEGA_ANT)
    ));
    // Combat
    public static final RegistryObject<Skill> MANDIBLES = SKILLS.register("mandibles", () -> new QuestSkill(
            Component.literal("Mandibles"),
            Component.literal("""
            An Item that acts like a diamond sword
            When right clicking a mob or player with the Mandibles, you can get the mobs head.
            When a mob or player has their head taken, they get blindness for 10 seconds"""),
            QuestInit.MANDIBLES
    ));
    public static final RegistryObject<Skill> ACID_SPRAY = SKILLS.register("acid_spray", () -> new QuestSkill(
            Component.literal("Acid Spray"),
            Component.literal("""
                    An item that looks like a glob of acid. When right clicked, acid shoots out from the ants body,
                    giving Poison I to anything it hits and dealing extreme damage to armor.
                    """),
            QuestInit.ACID_SPRAY
    ));
    public static final RegistryObject<Skill> VENOMOUS_STING = SKILLS.register("venomous_sting", () -> new QuestSkill(
            Component.literal("Venomous Sting"),
            Component.literal("""
                    A stinger like item that can be held in the hand. It can be used as a diamond sword like the Mandibles,
                    except it does the damage of a diamond axe, has no swinging cooldown, and when right clicked,
                    it can be held down and shot like a bow; that shoots out the stinger.
                    """),
            QuestInit.VENOMOUS_STING
    ));
    public static final RegistryObject<Skill> ANT_ARMY = SKILLS.register("ant_army", () -> new QuestSkill(
            Component.literal("Any Army"),
            Component.literal("10 ants will be summoned around you to fight for you. There can be an infinite number of ants at any given time."),
            QuestInit.ANT_ARMY
    ));
    public static final RegistryObject<Skill> MIND_CONTROL = SKILLS.register("mind_control", () -> new QuestSkill(
            Component.literal("Mind Control"),
            Component.literal("""
                    Right click on a player to make a sapling appear on their head. That player is now infected and loses all control of his character.
                    The infected players character should begin walking towards nearby players (except the person who did the infecting) and using kill aura on them.
                    """),
            QuestInit.MIND_CONTROL
    ));

    public static final RegistryObject<MenuProvidingTree> COMBAT_TREE = SKILL_TREES.register("combat", () -> new CombatTree(
            Component.literal("Combat"),
            Arrays.asList(MANDIBLES, ACID_SPRAY, VENOMOUS_STING, ANT_ARMY, MIND_CONTROL)
    ));
    // Utility
    public static final RegistryObject<Skill> CAMOUFLAGE = SKILLS.register("camouflage", () -> new SimpleSkill(
            Component.literal("Camouflage"),
            Component.literal("""
                    Whatever block is being looked at when this ability is used; the player will turn into that block.
                    Press the ability again to deactivate. When deactivated, you gain invisibility for 10 seconds.
                    """),
            Arrays.asList(
                    new ItemSkillRequirement(() -> Items.SEAGRASS, 16),
                    new ItemSkillRequirement(() -> Items.CHARCOAL, 16),
                    new ItemSkillRequirement(() -> Items.WHITE_DYE, 16)
            ),
            player -> {
                unlockAbility(player, AbilityInit.CAMOUFLAGE.get());
            },
            player -> {
                removeAbility(player, AbilityInit.CAMOUFLAGE.get());
            }
    ));
    public static final RegistryObject<Skill> LEAF_CUTTER_TOOLS = SKILLS.register("leaf_cutter_tools", () -> new SimpleSkill(
            Component.literal("Leaf Cutter Tools"),
            Component.literal("An unbreakable tool that can be shift right-clicked to turn into a diamond version of all of the following: " +
                    "Pickaxe, Shovel, Axe, Bow (with infinity), Flint and Steel, Ender Pearl, and shears."),
            Arrays.asList(
                    new ItemTagSkillRequirement(() -> ItemTags.DECORATED_POT_SHERDS, 1, Component.literal("Pottery Fragment")),
                    new ItemSkillRequirement(() -> Items.BONE_BLOCK, 16),
                    new ItemSkillRequirement(() -> Items.FIRE_CHARGE, 24)
            ),
            player -> {
                player.getInventory().add(new ItemStack(ItemInit.LEAF_CUTTER_TOOLS.get()));
            },
            player -> {
            }
    ));
    public static final RegistryObject<Skill> ECHO_LOCATION = SKILLS.register("echo_location", () -> new SimpleSkill(
            Component.literal("Echo Location"),
            Component.literal("An ability that when used; automatically gives me the coords in chat as to where the nearest selected ore is."),
            Arrays.asList(
                    new ItemTreasureMapSkillRequirement(1),
                    new ItemTagSkillRequirement(() -> ItemTags.TRIM_TEMPLATES, 1, Component.literal("Trim Template")),
                    new ItemSkillRequirement(() -> Items.GHAST_TEAR, 3)
            ),
            player -> {
                player.getInventory().add(new ItemStack(ItemInit.ECHO_LOCATION.get()));
            },
            player -> {

            }
    ));

    public static final RegistryObject<Skill> SWARM_SHIELD = SKILLS.register("swarm_shield", () -> new SimpleSkill(
            Component.literal("Swarm Shield"),
            Component.literal("""
                    When activated a group of ants run in circles around the users ant body.
                    These ants act as a shield that can absorb up to 50 damage.
                    """),
            Arrays.asList(
                    new ItemTagSkillRequirement(() -> TagInit.CORAL, 32, Component.literal("Coral")),
                    new ItemSkillRequirement(() -> Items.AXOLOTL_BUCKET, 3),
                    new ItemTagSkillRequirement(() -> TagInit.HORSE_ARMOR, 3, Component.literal("Horse Armor"))
            ),
            player -> {
                unlockAbility(player, AbilityInit.SWARM_SHIELD.get());
            },
            player -> {
                removeAbility(player, AbilityInit.SWARM_SHIELD.get());
            }
    ));
    public static final RegistryObject<Skill> GIGA_ANT = SKILLS.register("giga_ant", () -> new SimpleSkill(
            Component.literal("GIGA ANT"),
            Component.literal("""
                    When used; the ant now does double damage, has no cooldowns, has double speed, double strength, and double jump.
                    In addition the user now has Kill Aura, can see all players through walls, and can now reach an extra 20 blocks.
                    """),
            Arrays.asList(
                    new ItemSkillRequirement(() -> Items.DRAGON_EGG, 1),
                    new ItemSkillRequirement(() -> Items.WITHER_SKELETON_SKULL, 3),
                    new ItemSkillRequirement(() -> Items.SHULKER_BOX, 3)
            ),
            player -> {
                unlockAbility(player, AbilityInit.GIGA_ANT.get());
            },
            player -> {
                removeAbility(player, AbilityInit.GIGA_ANT.get());
            }
    ));

    public static final RegistryObject<MenuProvidingTree> UTILITY_TREE = SKILL_TREES.register("utility", () -> new UtilityTree(
            Component.literal("Utility"),
            Arrays.asList(CAMOUFLAGE, LEAF_CUTTER_TOOLS, ECHO_LOCATION, SWARM_SHIELD, GIGA_ANT)
    ));


    public static void unlockAbility(Player player, Ability abilityToUnlock) {
        AbilityHolderAttacher.getAbilityHolder(player).ifPresent(holder -> {
            int index = -1;
            boolean hasAbility = false;
            for (int i = 0; i < holder.getAbilitiesSize(); ++i) {
                Ability ability = holder.getAbility(i);
                if (index == -1 && ability == HMAAbilityInit.NONE.get()) {
                    index = i;
                }
                if (ability == abilityToUnlock) {
                    hasAbility = true;
                    break;
                }
            }
            if (index != -1 && !hasAbility) {
                holder.setAbility(index, abilityToUnlock);
            }
        });
    }

    public static void removeAbility(Player player, Ability ability) {
        AbilityHolderAttacher.getAbilityHolder(player).ifPresent(holder -> {
            int i = holder.getAbilities().indexOf(ability);
            if (i == -1) {
                return;
            }
            holder.setAbility(i, HMAAbilityInit.NONE.get());
        });
    }
}
