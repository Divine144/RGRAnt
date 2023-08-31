package com.divinity.hmedia.rgrant.event;

import com.divinity.hmedia.rgrant.RGRAnt;
import com.divinity.hmedia.rgrant.cap.AntHolderAttacher;
import com.divinity.hmedia.rgrant.client.AntAnimatable;
import com.divinity.hmedia.rgrant.client.renderer.AcidEntityRenderer;
import com.divinity.hmedia.rgrant.client.renderer.FakePlayerRenderer;
import com.divinity.hmedia.rgrant.client.renderer.StingerEntityRenderer;
import com.divinity.hmedia.rgrant.init.*;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import dev._100media.hundredmediageckolib.client.animatable.IHasGeoRenderer;
import dev._100media.hundredmediageckolib.client.animatable.MotionAttackAnimatable;
import dev._100media.hundredmediageckolib.client.animatable.SimpleAnimatable;
import dev._100media.hundredmediageckolib.client.model.SimpleGeoEntityModel;
import dev._100media.hundredmediageckolib.client.model.SimpleGeoPlayerModel;
import dev._100media.hundredmediageckolib.client.renderer.GeoPlayerRenderer;
import dev._100media.hundredmediageckolib.client.renderer.layer.GeoSeparatedEntityRenderLayer;
import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import dev._100media.hundredmediamorphs.client.renderer.MorphRenderers;
import dev._100media.hundredmediamorphs.morph.Morph;
import dev._100media.hundredmediaquests.client.screen.QuestSkillScreen;
import dev._100media.hundredmediaquests.client.screen.SkillScreen;
import dev._100media.hundredmediaquests.client.screen.TreeScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.Arrays;

@Mod.EventBusSubscriber(modid = RGRAnt.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEvents {

    public static final KeyMapping SKILL_TREE_KEY = new KeyMapping("key." + RGRAnt.MODID + ".skill_tree", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, "key.category." + RGRAnt.MODID);
    private static final ResourceLocation NET_OVERLAY = new ResourceLocation(RGRAnt.MODID, "textures/gui/overlay/net_overlay.png");
    @SubscribeEvent
    public static void registerKeybind(RegisterKeyMappingsEvent event) {
        event.register(SKILL_TREE_KEY);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityInit.MIND_CONTROLLED_PLAYER.get(), FakePlayerRenderer::new);
        event.registerEntityRenderer(EntityInit.ACID_ENTITY.get(), AcidEntityRenderer::new);
        event.registerEntityRenderer(EntityInit.STINGER_ENTITY.get(), StingerEntityRenderer::new);
        event.registerEntityRenderer(EntityInit.ANT_ENTITY.get(), ctx -> new GeoEntityRenderer<>(ctx, new SimpleGeoEntityModel<>(RGRAnt.MODID, "ant_entity")).withScale(0.4f));


        // Change this
        event.registerEntityRenderer(EntityInit.ANT_DRONE_ENTITY.get(), ctx -> new GeoEntityRenderer<>(ctx, new SimpleGeoEntityModel<>(RGRAnt.MODID, "ant_entity")).withScale(0.4f));



        // TODO : Change these
        createSimpleMorphRenderer(MorphInit.BABY_ANT.get(), "baby_ant", new AntAnimatable(), 1.0f);
        createSimpleMorphRenderer(MorphInit.BLACK_ANT.get(), "black_ant", new AntAnimatable(), 1.0f);
        createSimpleMorphRenderer(MorphInit.FIRE_ANT.get(), "baby_ant", new SimpleAnimatable(), 1.0f);
        createSimpleMorphRenderer(MorphInit.KING_ANT.get(), "baby_ant", new SimpleAnimatable(), 1.0f);
        createSimpleMorphRenderer(MorphInit.OMEGA_ANT.get(), "baby_ant", new SimpleAnimatable(), 1.0f);
    }

    @SubscribeEvent
    public static void initClient(FMLClientSetupEvent event) {
        MenuScreens.register(MenuInit.SKILL_TREE.get(), (AbstractContainerMenu menu, Inventory inv, Component title) -> new TreeScreen(menu, inv, title,
                new ResourceLocation(RGRAnt.MODID, "textures/gui/screen/skill_tree.png"), 21, 22,
                Arrays.asList(
                        new Pair<>(SkillInit.EVOLUTION_TREE, new Pair<>(56, 80)),
                        new Pair<>(SkillInit.COMBAT_TREE, new Pair<>(115, 80)),
                        new Pair<>(SkillInit.UTILITY_TREE, new Pair<>(170, 80))
                ), 256, 256, 256, 165
        ));
        MenuScreens.register(MenuInit.EVOLUTION_TREE.get(), (AbstractContainerMenu menu, Inventory inv, Component title) -> new SkillScreen(menu, inv, title,
                new ResourceLocation(RGRAnt.MODID, "textures/gui/screen/evolution.png"), 20, 20,
                Arrays.asList(
                        new Pair<>(38, 87),
                        new Pair<>(76, 87),
                        new Pair<>(114, 87),
                        new Pair<>(152, 87),
                        new Pair<>(188, 87)
                ), SkillInit.EVOLUTION_TREE.get(), 256, 256, 256, 230
        ));
        MenuScreens.register(MenuInit.COMBAT_TREE.get(), (AbstractContainerMenu menu, Inventory inv, Component title) -> new QuestSkillScreen(menu, inv, title,
                new ResourceLocation(RGRAnt.MODID, "textures/gui/screen/combat.png"), 17, 16,
                Arrays.asList(
                        new Pair<>(57, 74),
                        new Pair<>(98, 69),
                        new Pair<>(133, 64),
                        new Pair<>(168, 69),
                        new Pair<>(197, 75)
                ), SkillInit.COMBAT_TREE.get(), 256, 256, 256, 189
        ));
        MenuScreens.register(MenuInit.UTILITY_TREE.get(), (AbstractContainerMenu menu, Inventory inv, Component title) -> new SkillScreen(menu, inv, title,
                new ResourceLocation(RGRAnt.MODID, "textures/gui/screen/utility.png"), 21, 20,
                Arrays.asList(
                        new Pair<>(28, 90),
                        new Pair<>(76, 88),
                        new Pair<>(119, 91),
                        new Pair<>(167, 89),
                        new Pair<>(209, 91)
                ), SkillInit.UTILITY_TREE.get(), 256, 256, 256, 192
        ));
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerBelowAll("netted", (forgeGui, guiGraphics, partialTick, width, height) -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && player.hasEffect(EffectInit.NETTED.get())) {
                guiGraphics.blit(NET_OVERLAY, 0,0, width, height,0, 0, 256, 256, 256, 256);
            }
        });
    }

    private static <T extends IHasGeoRenderer & GeoAnimatable> void createSimpleMorphRenderer(Morph morph, String name, T animatable, float scale) {
        MorphRenderers.registerPlayerMorphRenderer(morph, context -> {
            var renderer = new GeoPlayerRenderer<>(context, new SimpleGeoPlayerModel<>(RGRAnt.MODID, name) {
                @Override
                public ResourceLocation getTextureResource(T animatable1, @Nullable AbstractClientPlayer player) {
                    return new ResourceLocation(RGRAnt.MODID, "textures/entity/" + name + ".png");
                }
            }, animatable) {

                @Override
                public void render(AbstractClientPlayer player, T animatable1, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
                    if (!player.hasEffect(MobEffects.INVISIBILITY)) {
                        var holder = AntHolderAttacher.getAntHolderUnwrap(player);
                        if (holder != null) {
                            poseStack.pushPose();
                            if (holder.getCamouflagedBlock() != Blocks.AIR) {
                                poseStack.translate(-0.5, 0, -0.5);
                                Minecraft.getInstance().getBlockRenderer().renderSingleBlock(holder.getCamouflagedBlock().defaultBlockState(), poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
                            }
                            else {
                                if (player.getVehicle() != null) {
                                    poseStack.translate(0, 0, 0);
                                }
                                poseStack.scale(scale, scale, scale);
                                super.render(player, animatable1, entityYaw, partialTick, poseStack, bufferSource, packedLight);
                            }
                            poseStack.popPose();

                        }
                    }
                }
            };
            renderer.addRenderLayer(new GeoSeparatedEntityRenderLayer<>(renderer) {
                private static final ResourceLocation SHIELD_MODEL = new ResourceLocation(RGRAnt.MODID, "geo/entity/ant_shield.geo.json");
                private static final ResourceLocation SHIELD_TEXTURE = new ResourceLocation(RGRAnt.MODID, "textures/entity/ant_shield.png");

                @Override
                public void renderPre(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, AbstractClientPlayer entity, T animatable, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
                    AntHolderAttacher.getAntHolder(entity).ifPresent(cap -> {
                        if (cap.getRemainingShield() <= 0) {
                            return;
                        }
                        RenderType renderType =  RenderType.entityCutout(SHIELD_TEXTURE);
                        poseStack.pushPose();
                        MorphHolderAttacher.getCurrentMorph(entity).ifPresent(m -> {
                            if (m == MorphInit.BABY_ANT.get()) {
                                poseStack.scale(1, 1, 1);
                            }
                            else if (m == MorphInit.BLACK_ANT.get()) {
                                poseStack.scale(2F, 2F, 2F);
                            }
                            else if (m == MorphInit.FIRE_ANT.get()) {
                                poseStack.scale(3.5F, 3.5F, 3.5F);
                            }
                            else if (m == MorphInit.KING_ANT.get()) {
                                poseStack.scale(3.5F, 3.5F, 3.5F);
                            }
                            else if (m == MorphInit.OMEGA_ANT.get()) {
                                poseStack.scale(3.5F, 3.5F, 3.5F);
                            }
                        });
                        renderer.reRender(this.getEntityModel().getBakedModel(SHIELD_MODEL), poseStack, bufferSource, animatable, renderType, bufferSource.getBuffer(renderType), partialTick, packedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
                        poseStack.popPose();
                    });
                }
            });
            return renderer;
        });
    }
}
