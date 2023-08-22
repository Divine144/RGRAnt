package com.divinity.hmedia.rgrant.cap;

import com.divinity.hmedia.rgrant.RGRAnt;
import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RGRAnt.MODID)
public class AntHolderAttacher extends CapabilityAttacher {
    public static final Capability<AntHolder> EXAMPLE_CAPABILITY = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation EXAMPLE_RL = new ResourceLocation(RGRAnt.MODID, "example");
    private static final Class<AntHolder> CAPABILITY_CLASS = AntHolder.class;

    @SuppressWarnings("ConstantConditions")
    public static AntHolder getExampleHolderUnwrap(Entity player) {
        return getExampleHolder(player).orElse(null);
    }

    public static LazyOptional<AntHolder> getExampleHolder(Entity player) {
        return player.getCapability(EXAMPLE_CAPABILITY);
    }

    private static void attach(AttachCapabilitiesEvent<Entity> event, Entity entity) {
        genericAttachCapability(event, new AntHolder(entity), EXAMPLE_CAPABILITY, EXAMPLE_RL);
    }

    public static void register() {
        CapabilityAttacher.registerCapability(CAPABILITY_CLASS);
        CapabilityAttacher.registerEntityAttacher(LivingEntity.class, AntHolderAttacher::attach, AntHolderAttacher::getExampleHolder);
    }
}
