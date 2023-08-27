package com.divinity.hmedia.rgrant.init;

import com.divinity.hmedia.rgrant.RGRAnt;
import com.divinity.hmedia.rgrant.ability.AntArmyAbility;
import dev._100media.hundredmediaabilities.HundredMediaAbilitiesMod;
import dev._100media.hundredmediaabilities.ability.Ability;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class AbilityInit {

    public static final DeferredRegister<Ability> ABILITIES = DeferredRegister.create(new ResourceLocation(HundredMediaAbilitiesMod.MODID, "abilities"), RGRAnt.MODID);

    public static final RegistryObject<Ability> ANT_ARMY = ABILITIES.register("ant_army", AntArmyAbility::new);
    public static final RegistryObject<Ability> CAMOUFLAGE = ABILITIES.register("camouflage", Ability::new);

    public static final RegistryObject<Ability> SWARM_SHIELD = ABILITIES.register("swarm_shield", Ability::new);

    public static final RegistryObject<Ability> GIGA_ANT = ABILITIES.register("giga_ant", Ability::new);



}
