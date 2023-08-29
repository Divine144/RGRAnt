package com.divinity.hmedia.rgrant.init;

import com.divinity.hmedia.rgrant.RGRAnt;
import com.divinity.hmedia.rgrant.effect.NettedEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EffectInit {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, RGRAnt.MODID);

    public static final RegistryObject<MobEffect> NETTED = EFFECTS.register("netted", () -> new NettedEffect(MobEffectCategory.HARMFUL, 0xFF964B00));
}
