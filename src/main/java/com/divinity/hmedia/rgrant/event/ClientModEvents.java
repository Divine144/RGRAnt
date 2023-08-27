package com.divinity.hmedia.rgrant.event;

import com.divinity.hmedia.rgrant.RGRAnt;
import com.divinity.hmedia.rgrant.client.renderer.FakePlayerRenderer;
import com.divinity.hmedia.rgrant.init.EntityInit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RGRAnt.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityInit.MIND_CONTROLLED_PLAYER.get(), FakePlayerRenderer::new);
    }
}
