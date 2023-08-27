package com.divinity.hmedia.rgrant.init;

import com.divinity.hmedia.rgrant.RGRAnt;
import dev._100media.hundredmediamorphs.HundredMediaMorphsMod;
import dev._100media.hundredmediamorphs.morph.Morph;
import dev._100media.hundredmediamorphs.skin.SkinType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MorphInit {
    public static final DeferredRegister<Morph> MORPHS = DeferredRegister.create(new ResourceLocation(HundredMediaMorphsMod.MODID, "morphs"), RGRAnt.MODID);

    public static final RegistryObject<Morph> BABY_ANT = MORPHS.register("baby_ant", () -> new Morph(new Morph.Properties<>()
            .maxHealth(10)
            .swingDuration(7)
            .dimensions(0.65f, 0.65f)
            .eyeHeight(0.5f)
            .morphedTo(entity -> {
                entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, -1, 0, false, false, false));
            })
            .demorph(entity -> entity.removeEffect(MobEffects.DIG_SPEED))
    ));
    public static final RegistryObject<Morph> BLACK_ANT = MORPHS.register("black_ant", () -> new Morph(new Morph.Properties<>()
            .maxHealth(20)
            .swingDuration(7)
            .dimensions(1f, 1f)
            .morphedTo(entity -> {
                entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 0, false, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 0, false, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, -1, 1, false, false, false));
            })
            .demorph(entity -> {
                entity.removeEffect(MobEffects.MOVEMENT_SPEED);
                entity.removeEffect(MobEffects.DAMAGE_BOOST);
                entity.removeEffect(MobEffects.DIG_SPEED);
            })
    ));
    public static final RegistryObject<Morph> FIRE_ANT = MORPHS.register("fire_ant", () -> new Morph(new Morph.Properties<>()
            .maxHealth(40)
            .swingDuration(7)
            .dimensions(1.5f, 2f)
            .morphedTo(entity -> {
                entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 1, false, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, -1, 1, false, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, -1, 0, false, false, false));
            })
            .demorph(entity -> {
                entity.removeEffect(MobEffects.MOVEMENT_SPEED);
                entity.removeEffect(MobEffects.DAMAGE_BOOST);
                entity.removeEffect(MobEffects.DIG_SPEED);
                entity.removeEffect(MobEffects.FIRE_RESISTANCE);
            })
    ));
    public static final RegistryObject<Morph> KING_ANT = MORPHS.register("king_ant", () -> new Morph(new Morph.Properties<>()
            .maxHealth(60)
            .swingDuration(7)
            .dimensions(2f, 3f)
            .morphedTo(entity -> {
                entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 2, false, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 2, false, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, -1, 1, false, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, -1, 0, false, false, false));
                var reachDistance = entity.getAttribute(ForgeMod.BLOCK_REACH.get());
                var attackDistance = entity.getAttribute(ForgeMod.ENTITY_REACH.get());
                if (reachDistance != null && attackDistance != null) {
                    reachDistance.setBaseValue(reachDistance.getAttribute().getDefaultValue() + 3);
                    attackDistance.setBaseValue(attackDistance.getAttribute().getDefaultValue() + 3);
                }
            })
            .demorph(entity -> {
                entity.removeEffect(MobEffects.MOVEMENT_SPEED);
                entity.removeEffect(MobEffects.DAMAGE_BOOST);
                entity.removeEffect(MobEffects.DIG_SPEED);
                entity.removeEffect(MobEffects.FIRE_RESISTANCE);
                var reachDistance = entity.getAttribute(ForgeMod.BLOCK_REACH.get());
                var attackDistance = entity.getAttribute(ForgeMod.ENTITY_REACH.get());
                if (reachDistance != null && attackDistance != null) {
                    reachDistance.setBaseValue(reachDistance.getAttribute().getDefaultValue());
                    attackDistance.setBaseValue(attackDistance.getAttribute().getDefaultValue());
                }
            })
    ));
    public static final RegistryObject<Morph> OMEGA_ANT = MORPHS.register("omega_ant", () -> new Morph(new Morph.Properties<>()
            .maxHealth(100)
            .swingDuration(7)
            .dimensions(2f, 6f)
            .morphedTo(entity -> {
                entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 4, false, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 4, false, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.JUMP, -1, 3, false, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, -1, 1, false, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, -1, 0, false, false, false));
                var reachDistance = entity.getAttribute(ForgeMod.BLOCK_REACH.get());
                var attackDistance = entity.getAttribute(ForgeMod.ENTITY_REACH.get());
                if (reachDistance != null && attackDistance != null) {
                    reachDistance.setBaseValue(reachDistance.getAttribute().getDefaultValue() + 5);
                    attackDistance.setBaseValue(attackDistance.getAttribute().getDefaultValue() + 5);
                }
            })
            .demorph(entity -> {
                entity.removeEffect(MobEffects.MOVEMENT_SPEED);
                entity.removeEffect(MobEffects.DAMAGE_BOOST);
                entity.removeEffect(MobEffects.DIG_SPEED);
                entity.removeEffect(MobEffects.JUMP);
                entity.removeEffect(MobEffects.FIRE_RESISTANCE);
                var reachDistance = entity.getAttribute(ForgeMod.BLOCK_REACH.get());
                var attackDistance = entity.getAttribute(ForgeMod.ENTITY_REACH.get());
                if (reachDistance != null && attackDistance != null) {
                    reachDistance.setBaseValue(reachDistance.getAttribute().getDefaultValue());
                    attackDistance.setBaseValue(attackDistance.getAttribute().getDefaultValue());
                }
            })
    ));
}