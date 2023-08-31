package com.divinity.hmedia.rgrant.init;

import com.divinity.hmedia.rgrant.RGRAnt;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundInit {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, RGRAnt.MODID);

    public static final RegistryObject<SoundEvent> BUG_SPRAY = sound("bug_spray");
    public static final RegistryObject<SoundEvent> ACID_SPRAY = sound("acid_spray");
    public static final RegistryObject<SoundEvent> ANT_DRONE_WARNING = sound("ant_drone_warning");
    public static final RegistryObject<SoundEvent> ECHO_LOCATION = sound("echo_location");
    public static final RegistryObject<SoundEvent> LEAF_TOOLS = sound("leaf_cutter_tools");
    public static final RegistryObject<SoundEvent> MANDIBLES = sound("mandibles");
    public static final RegistryObject<SoundEvent> MIND_CONTROL = sound("mind_control");
    public static final RegistryObject<SoundEvent> SWARM_SHIELD = sound("swarm_shield");
    public static final RegistryObject<SoundEvent> STING = sound("venomous_sting");

    private static RegistryObject<SoundEvent> sound(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(RGRAnt.MODID, name)));
    }
}
