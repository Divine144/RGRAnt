package com.divinity.hmedia.rgrant.item;

import com.divinity.hmedia.rgrant.RGRAnt;
import com.divinity.hmedia.rgrant.init.SoundInit;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev._100media.hundredmediageckolib.item.animated.AnimatedItemProperties;
import dev._100media.hundredmediageckolib.item.animated.SimpleAnimatedItem;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

import static net.minecraft.world.item.BowItem.getPowerForTime;

public class LeafCutterToolsItem extends SimpleAnimatedItem {

    private ToolTypes toolType;
    // pick -> shovel -> axe -> bow -> flint -> pearl -> shears

    private static final RawAnimation PICK_IDLE = RawAnimation.begin().thenLoop("pickaxe-IDLE");
    private static final RawAnimation SHOVEL_IDLE = RawAnimation.begin().thenLoop("shovel-IDLE");
    private static final RawAnimation AXE_IDLE = RawAnimation.begin().thenLoop("axe_IDLE");
    private static final RawAnimation BOW_IDLE = RawAnimation.begin().thenLoop("bow-IDLE");

    private static final RawAnimation BOW_LOAD = RawAnimation.begin()
            .then("bow-LOAD", Animation.LoopType.PLAY_ONCE)
            .thenLoop("bow-LOADED");

    private static final RawAnimation BOW_SHOOT = RawAnimation.begin()
            .then("bow-SHOOT", Animation.LoopType.PLAY_ONCE)
            .thenLoop("bow-IDLE");

    private static final RawAnimation FLINT_IDLE = RawAnimation.begin().thenLoop("flintsteel-ON");
    private static final RawAnimation PEARL_IDLE = RawAnimation.begin().thenLoop("bucket_IDLE-EMPTY");
    private static final RawAnimation SHEARS_IDLE = RawAnimation.begin().thenLoop("shears-IDLE");

    private static final RawAnimation PICK_TO_SHOVEL = RawAnimation.begin()
            .then("pickaxe-CLOSE", Animation.LoopType.PLAY_ONCE)
            .then("shovel-RELEASE", Animation.LoopType.PLAY_ONCE)
            .thenLoop("shovel-IDLE");

    private static final RawAnimation SHOVEL_TO_AXE = RawAnimation.begin()
            .then("shovel-CLOSE", Animation.LoopType.PLAY_ONCE)
            .then("axe_RELEASE", Animation.LoopType.PLAY_ONCE)
            .thenLoop("axe_IDLE");

    private static final RawAnimation AXE_TO_BOW = RawAnimation.begin()
            .then("axe_CLOSE", Animation.LoopType.PLAY_ONCE)
            .then("bow-RELEASE", Animation.LoopType.PLAY_ONCE)
            .thenLoop("bow-IDLE");

    private static final RawAnimation BOW_TO_FLINT = RawAnimation.begin()
            .then("bow-CLOSE", Animation.LoopType.PLAY_ONCE)
            .then("flintsteel-RELEASE", Animation.LoopType.PLAY_ONCE)
            .thenLoop("flintsteel-ON");

    private static final RawAnimation FLINT_TO_PEARL = RawAnimation.begin()
            .then("flintsteel-TURNOFF", Animation.LoopType.PLAY_ONCE)
            .then("enderpearl-RELEASE", Animation.LoopType.PLAY_ONCE)
            .thenLoop("enderpearl-idle");

    private static final RawAnimation PEARL_TO_SHEARS = RawAnimation.begin()
            .then("enderpearl-TURNOFF", Animation.LoopType.PLAY_ONCE)
            .then("shears-RELEASE", Animation.LoopType.PLAY_ONCE)
            .thenLoop("shears-IDLE");

    private static final RawAnimation SHEARS_TO_PICK = RawAnimation.begin()
            .then("shears-CLOSE", Animation.LoopType.PLAY_ONCE)
            .then("pickaxe-RELEASE", Animation.LoopType.PLAY_ONCE)
            .thenLoop("pickaxe-IDLE");


    public LeafCutterToolsItem(AnimatedItemProperties properties) {
        super(properties);
        this.toolType = ToolTypes.PICK;
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, event -> {
            var data = event.getData(DataTickets.ITEMSTACK);
            if (data != null && data.getItem() == this) {
                CompoundTag tag = data.getShareTag();
                if (tag != null) {
                    int cycle = tag.getInt("cycle");
                    return switch (ToolTypes.values()[cycle]) {
                        case PICK -> event.setAndContinue(PICK_IDLE);
                        case SHOVEL -> event.setAndContinue(SHOVEL_IDLE);
                        case AXE -> event.setAndContinue(AXE_IDLE);
                        case BOW -> event.setAndContinue(BOW_IDLE);
                        case FLINT -> event.setAndContinue(FLINT_IDLE);
                        case PEARL -> event.setAndContinue(PEARL_IDLE);
                        case SHEARS -> event.setAndContinue(SHEARS_IDLE);
                    };
                }
                else return event.setAndContinue(PICK_IDLE);
            }
            return PlayState.CONTINUE;
        }).triggerableAnim("pickToShovel", PICK_TO_SHOVEL)
          .triggerableAnim("shovelToAxe", SHOVEL_TO_AXE)
          .triggerableAnim("axeToBow", AXE_TO_BOW)
          .triggerableAnim("bowToFlint", BOW_TO_FLINT)
          .triggerableAnim("flintToPearl", FLINT_TO_PEARL)
          .triggerableAnim("pearlToShears", PEARL_TO_SHEARS)
          .triggerableAnim("shearsToPick", SHEARS_TO_PICK)
          .triggerableAnim("bowLoading", BOW_LOAD)
          .triggerableAnim("bowShoot", BOW_SHOOT)
        );
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        int ordinal = tag.getInt("cycle");
        return slot == EquipmentSlot.MAINHAND ? ToolTypes.values()[ordinal].modifierMultimap : ImmutableMultimap.of();
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null)
                    this.renderer = new GeoItemRenderer<LeafCutterToolsItem>(new DefaultedItemGeoModel<>(new ResourceLocation(RGRAnt.MODID, "leaf_cutter_tools"))) {

                        @Override
                        public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
                            poseStack.pushPose();
                            switch (transformType) {
                                case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {}
                                case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> {
                                    poseStack.mulPose(Axis.YP.rotationDegrees(90));
                                    poseStack.scale(0.75f, 0.75f, 0.75f);
                                    poseStack.translate(0, -0.8, 0.75);
                                }
                            }
                            super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
                            poseStack.popPose();
                        }

                        @Override
                        protected void renderInGui(ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
                            poseStack.pushPose();
                            if (currentItemStack != null && animatable != null) {
                                CompoundTag tag = animatable.getShareTag(currentItemStack);
                                if (tag != null) {
                                    int cycle = tag.getInt("cycle");
                                    ToolTypes type = ToolTypes.values()[cycle];
                                    if (type == ToolTypes.BOW) {
                                        poseStack.scale(0.2f, 0.2f, 0.2f);
                                        poseStack.mulPose(Axis.ZN.rotationDegrees(-45));
                                        poseStack.translate(3.5, -1.25, 0);
                                    }
                                    else {
                                        poseStack.scale(0.3f, 0.3f, 0.3f);
                                        poseStack.translate(1.2, -0.2, 0);
                                    }
                                }
                            }
                            super.renderInGui(transformType, poseStack, bufferSource, packedLight, packedOverlay);
                            poseStack.popPose();
                        }
                    };
                return this.renderer;
            }
        });
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity, int pTimeCharged) {
        CompoundTag tag = pStack.getOrCreateTag();
        int cycle = tag.getInt("cycle");
        if (ToolTypes.values()[cycle] == ToolTypes.BOW) {
            if (pLivingEntity instanceof Player player) {
                ItemStack itemstack = new ItemStack(Items.ARROW);
                int i = this.getUseDuration(pStack) - pTimeCharged;
                if (i < 0) return;
                float f = getPowerForTime(i);
                if (!((double) f < 0.1D)) {
                    if (!pLevel.isClientSide) {
                        ArrowItem arrowitem = (ArrowItem) (itemstack.getItem() instanceof ArrowItem ? itemstack.getItem() : Items.ARROW);
                        AbstractArrow abstractarrow = arrowitem.createArrow(pLevel, itemstack, player);
                        abstractarrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, f * 3.0F, 1.0F);
                        if (f == 1.0F) {
                            abstractarrow.setCritArrow(true);
                        }
                        abstractarrow.pickup = AbstractArrow.Pickup.DISALLOWED;
                        pLevel.addFreshEntity(abstractarrow);
                        if (player instanceof ServerPlayer serverPlayer) {
                            triggerAnim(serverPlayer, GeoItem.getOrAssignId(pStack, serverPlayer.serverLevel()), "controller", "bowShoot");
                        }
                    }
                    pLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (pLevel.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                    player.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (pLevel.isClientSide) {
            return InteractionResultHolder.consume(itemStack);
        }
        CompoundTag tag = itemStack.getOrCreateTag();
        if (pPlayer.isShiftKeyDown()) {
            // pick -> shovel -> axe -> bow -> flint -> pearl -> shears
            String animName = switch (ToolTypes.values()[tag.getInt("cycle")]) {
                case PICK -> "pickToShovel";
                case SHOVEL -> "shovelToAxe";
                case AXE -> "axeToBow";
                case BOW -> "bowToFlint";
                case FLINT -> "flintToPearl";
                case PEARL -> "pearlToShears";
                case SHEARS -> "shearsToPick";
            };
            triggerAnim(pPlayer, GeoItem.getOrAssignId(itemStack, (ServerLevel) pLevel), "controller", animName);
            this.toolType = toolType.next();
            tag.putInt("cycle", toolType.ordinal());
            pPlayer.level().playSound(null, pPlayer.blockPosition(), SoundInit.LEAF_TOOLS.get(), SoundSource.PLAYERS, 0.5f, 1f);
            pPlayer.displayClientMessage(Component.literal("Tool changed to: " + toolType.name()).withStyle(ChatFormatting.GREEN), true);
        }
        else {
            if (ToolTypes.values()[tag.getInt("cycle")] == ToolTypes.PEARL) {
                pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.ENDER_PEARL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
                pPlayer.getCooldowns().addCooldown(this, 20);
                ThrownEnderpearl thrownenderpearl = new ThrownEnderpearl(pLevel, pPlayer);
                thrownenderpearl.setItem(Items.ENDER_PEARL.getDefaultInstance());
                thrownenderpearl.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, 1.5F, 1.0F);
                pLevel.addFreshEntity(thrownenderpearl);
                pPlayer.awardStat(Stats.ITEM_USED.get(this));
                return InteractionResultHolder.sidedSuccess(itemStack, pLevel.isClientSide());
            }
            else if (ToolTypes.values()[tag.getInt("cycle")] == ToolTypes.BOW) {
                triggerAnim(pPlayer, GeoItem.getOrAssignId(itemStack, (ServerLevel) pLevel), "controller", "bowLoading");
                return ItemUtils.startUsingInstantly(pLevel, pPlayer, pUsedHand);
            }
        }
        return InteractionResultHolder.pass(itemStack);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity entity, InteractionHand hand) {
        int cycle = stack.getOrCreateTag().getInt("cycle");
        if (ToolTypes.values()[cycle] == ToolTypes.SHEARS) {
            if (entity instanceof IForgeShearable target) {
                if (entity.level().isClientSide) return InteractionResult.SUCCESS;
                BlockPos pos = BlockPos.containing(entity.position());
                if (target.isShearable(stack, entity.level(), pos)) {
                    List<ItemStack> drops = target.onSheared(playerIn, stack, entity.level(), pos,
                            EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, stack));
                    Random rand = new Random();
                    drops.forEach(d -> {
                        ItemEntity ent = entity.spawnAtLocation(d, 1.0F);
                        if (ent != null) {
                            ent.setDeltaMovement(ent.getDeltaMovement().add((rand.nextFloat() - rand.nextFloat()) * 0.1F, rand.nextFloat() * 0.05F, (double) ((rand.nextFloat() - rand.nextFloat()) * 0.1F)));
                        }
                    });
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean canAttackBlock(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
        return !pPlayer.isCreative();
    }

    @Override
    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        int cycle = pStack.getOrCreateTag().getInt("cycle");
        return switch (ToolTypes.values()[cycle]) {
            case PICK -> pState.is(BlockTags.MINEABLE_WITH_PICKAXE) ? Tiers.DIAMOND.getSpeed() : 1.0F;
            case SHOVEL -> pState.is(BlockTags.MINEABLE_WITH_SHOVEL) ? Tiers.DIAMOND.getSpeed() : 1.0F;
            case AXE -> pState.is(BlockTags.MINEABLE_WITH_AXE) ? Tiers.DIAMOND.getSpeed() : 1.0F;
            case SHEARS -> {
                if (!pState.is(Blocks.COBWEB) && !pState.is(BlockTags.LEAVES)) {
                    if (pState.is(BlockTags.WOOL)) {
                        yield 5.0F;
                    }
                    else yield !pState.is(Blocks.VINE) && !pState.is(Blocks.GLOW_LICHEN) ? super.getDestroySpeed(pStack, pState) : 2.0F;
                }
                else yield 15.0F;
            }
            default -> 1.0F;
        };
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        BlockPos blockpos = pContext.getClickedPos();
        Player player = pContext.getPlayer();
        BlockState blockstate = level.getBlockState(blockpos);
        Block block = blockstate.getBlock();
        int cycle = pContext.getItemInHand().getOrCreateTag().getInt("cycle");
        switch (ToolTypes.values()[cycle]) {
            case AXE -> {
                Optional<BlockState> optional = Optional.ofNullable(blockstate.getToolModifiedState(pContext, ToolActions.AXE_STRIP, false));
                Optional<BlockState> optional1 = optional.isPresent() ? Optional.empty() : Optional.ofNullable(blockstate.getToolModifiedState(pContext, ToolActions.AXE_SCRAPE, false));
                Optional<BlockState> optional2 = optional.isPresent() || optional1.isPresent() ? Optional.empty() : Optional.ofNullable(blockstate.getToolModifiedState(pContext, ToolActions.AXE_WAX_OFF, false));
                ItemStack itemstack = pContext.getItemInHand();
                Optional<BlockState> optional3 = Optional.empty();
                if (optional.isPresent()) {
                    level.playSound(player, blockpos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
                    optional3 = optional;
                }
                else if (optional1.isPresent()) {
                    level.playSound(player, blockpos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.levelEvent(player, 3005, blockpos, 0);
                    optional3 = optional1;
                }
                else if (optional2.isPresent()) {
                    level.playSound(player, blockpos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.levelEvent(player, 3004, blockpos, 0);
                    optional3 = optional2;
                }
                if (optional3.isPresent()) {
                    if (player instanceof ServerPlayer) {
                        CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer) player, blockpos, itemstack);
                    }
                    level.setBlock(blockpos, optional3.get(), 11);
                    level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(player, optional3.get()));
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
                else {
                    return InteractionResult.PASS;
                }
            }
            case SHOVEL -> {
                if (pContext.getClickedFace() == Direction.DOWN) {
                    return InteractionResult.PASS;
                }
                else {
                    BlockState blockstate1 = blockstate.getToolModifiedState(pContext, ToolActions.SHOVEL_FLATTEN, false);
                    BlockState blockstate2 = null;
                    if (blockstate1 != null && level.isEmptyBlock(blockpos.above())) {
                        level.playSound(player, blockpos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
                        blockstate2 = blockstate1;
                    }
                    else if (blockstate.getBlock() instanceof CampfireBlock && blockstate.getValue(CampfireBlock.LIT)) {
                        if (!level.isClientSide()) {
                            level.levelEvent(null, 1009, blockpos, 0);
                        }
                        CampfireBlock.dowse(pContext.getPlayer(), level, blockpos, blockstate);
                        blockstate2 = blockstate.setValue(CampfireBlock.LIT, Boolean.FALSE);
                    }
                    if (blockstate2 != null) {
                        if (!level.isClientSide) {
                            level.setBlock(blockpos, blockstate2, 11);
                            level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(player, blockstate2));
                        }
                        return InteractionResult.sidedSuccess(level.isClientSide);
                    }
                    else {
                        return InteractionResult.PASS;
                    }
                }
            }
            case SHEARS -> {
                if (block instanceof GrowingPlantHeadBlock growingplantheadblock) {
                    if (!growingplantheadblock.isMaxAge(blockstate)) {
                        ItemStack itemstack = pContext.getItemInHand();
                        if (player instanceof ServerPlayer) {
                            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer) player, blockpos, itemstack);
                        }
                        level.playSound(player, blockpos, SoundEvents.GROWING_PLANT_CROP, SoundSource.BLOCKS, 1.0F, 1.0F);
                        BlockState blockstate1 = growingplantheadblock.getMaxAgeState(blockstate);
                        level.setBlockAndUpdate(blockpos, blockstate1);
                        level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(pContext.getPlayer(), blockstate1));
                        return InteractionResult.sidedSuccess(level.isClientSide);
                    }
                }
            }
            case FLINT -> {
                if (!CampfireBlock.canLight(blockstate) && !CandleBlock.canLight(blockstate) && !CandleCakeBlock.canLight(blockstate)) {
                    BlockPos blockpos1 = blockpos.relative(pContext.getClickedFace());
                    if (BaseFireBlock.canBePlacedAt(level, blockpos1, pContext.getHorizontalDirection())) {
                        level.playSound(player, blockpos1, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
                        BlockState blockstate1 = BaseFireBlock.getState(level, blockpos1);
                        level.setBlock(blockpos1, blockstate1, 11);
                        level.gameEvent(player, GameEvent.BLOCK_PLACE, blockpos);
                        ItemStack itemstack = pContext.getItemInHand();
                        if (player instanceof ServerPlayer) {
                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) player, blockpos1, itemstack);
                        }
                        return InteractionResult.sidedSuccess(level.isClientSide());
                    }
                    else {
                        return InteractionResult.FAIL;
                    }
                }
                else {
                    level.playSound(player, blockpos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
                    level.setBlock(blockpos, blockstate.setValue(BlockStateProperties.LIT, Boolean.valueOf(true)), 11);
                    level.gameEvent(player, GameEvent.BLOCK_CHANGE, blockpos);
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        int cycle = stack.getOrCreateTag().getInt("cycle");
        return switch (ToolTypes.values()[cycle]) {
            case PICK -> ToolActions.DEFAULT_PICKAXE_ACTIONS.contains(toolAction);
            case AXE -> ToolActions.DEFAULT_AXE_ACTIONS.contains(toolAction);
            case SHOVEL -> ToolActions.DEFAULT_SHOVEL_ACTIONS.contains(toolAction);
            case SHEARS -> ToolActions.DEFAULT_SHEARS_ACTIONS.contains(toolAction);
            default -> false;
        };
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        int cycle = pStack.getOrCreateTag().getInt("cycle");
        if (ToolTypes.values()[cycle] == ToolTypes.BOW) {
            return 72000;
        }
        return super.getUseDuration(pStack);
    }

    @Override
    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pEntityLiving) {
        return true;
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState pBlock) {
        return true;
    }

    private enum ToolTypes {
        PICK(ImmutableMultimap.<Attribute, AttributeModifier>builder()
                .put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 1 + Tiers.DIAMOND.getAttackDamageBonus(), AttributeModifier.Operation.ADDITION))
                .put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2.8F, AttributeModifier.Operation.ADDITION))
                .build()),
        SHOVEL(ImmutableMultimap.<Attribute, AttributeModifier>builder()
                .put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 1.5f + Tiers.DIAMOND.getAttackDamageBonus(), AttributeModifier.Operation.ADDITION))
                .put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -3F, AttributeModifier.Operation.ADDITION))
                .build()),
        AXE(ImmutableMultimap.<Attribute, AttributeModifier>builder()
                .put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 5.0f + Tiers.DIAMOND.getAttackDamageBonus(), AttributeModifier.Operation.ADDITION))
                .put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -3F, AttributeModifier.Operation.ADDITION))
                .build()),
        BOW(),
        FLINT(),
        PEARL(),
        SHEARS();

        private final Multimap<Attribute, AttributeModifier> modifierMultimap;

        ToolTypes(Multimap<Attribute, AttributeModifier> modifierMultimap) {
            this.modifierMultimap = modifierMultimap;
        }

        ToolTypes() {
            this(ImmutableMultimap.of());
        }

        public ToolTypes next() {
            return values()[(ordinal() + 1) % values().length];
        }
    }
}
