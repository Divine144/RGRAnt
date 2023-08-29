package com.divinity.hmedia.rgrant.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev._100media.hundredmediageckolib.item.animated.AnimatedItemProperties;
import dev._100media.hundredmediageckolib.item.animated.SimpleAnimatedItem;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static net.minecraft.world.item.BowItem.getPowerForTime;

public class LeafCutterToolsItem extends SimpleAnimatedItem {

    private ToolTypes toolType;
    public LeafCutterToolsItem(AnimatedItemProperties properties) {
        super(properties);
        this.toolType = ToolTypes.PICK;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        int ordinal = tag.getInt("cycle");
        return slot == EquipmentSlot.MAINHAND ? ToolTypes.values()[ordinal].modifierMultimap : ImmutableMultimap.of();
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
            // TODO: Add sound effect
            this.toolType = toolType.next();
            pPlayer.displayClientMessage(Component.literal("Tool changed to: " + toolType.name()).withStyle(ChatFormatting.GREEN), true);
            tag.putInt("cycle", toolType.ordinal());
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
