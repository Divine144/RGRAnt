package com.divinity.hmedia.rgrant.item;

import dev._100media.hundredmediamorphs.capability.MorphHolderAttacher;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;
import java.util.List;

public class EchoLocationItem extends Item {
    public EchoLocationItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if (entity.level().isClientSide)
            return false;

        if (entity.isShiftKeyDown()) {
            CompoundTag tag = stack.getOrCreateTag();
            OreType type = (tag.contains("OreType") ? OreType.values()[tag.getInt("OreType")] : OreType.COAL).next();
            tag.putInt("OreType", type.ordinal());
            if (entity instanceof Player player)
                player.displayClientMessage(Component.literal(type.name()).withStyle(Style.EMPTY.withColor(type.textColor)), true);
            return true;
        }
        return false;
    }

    // TODO: Add sound
    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (pLevel.isClientSide)
            return InteractionResultHolder.pass(itemStack);

        CompoundTag tag = itemStack.getOrCreateTag();
        OreType type = tag.contains("OreType") ? OreType.values()[tag.getInt("OreType")] : OreType.COAL;
        BlockPos base = pPlayer.blockPosition();
        BlockPos found = null;

        rangeLoop:
        for (int range = 1; range <= 32; range++) {
            for (int y = pLevel.getMinBuildHeight(); y < pLevel.getMaxBuildHeight(); y++) {
                for (int x = -range; x <= range; x++) {
                    for (int z = -range; z <= range; z++) {
                        BlockPos pos = base.offset(x, y, z);
                        if (type.is(pLevel.getBlockState(pos))) {
                            found = pos;
                            break rangeLoop;
                        }
                    }
                }
            }
        }
        if (found == null)
            pPlayer.displayClientMessage(Component.literal(type.name() + " not found."), false);
        else {
            pPlayer.displayClientMessage(Component.literal(type.name() + " found: " + found.getX() + " " + found.getY() + " " + found.getZ())
                    .withStyle(Style.EMPTY.withColor(type.textColor)), false);
        }
        return InteractionResultHolder.consume(itemStack);
    }

    // TODO: FIX
    @SuppressWarnings("ConstantConditions")
    static enum OreType {
        COAL(ChatFormatting.BLACK.getColor(), Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE),
        COPPER(ChatFormatting.GOLD.getColor(), Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE),
        IRON(ChatFormatting.WHITE.getColor(), Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE),
        GOLD(ChatFormatting.YELLOW.getColor(), Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE),
        LAPIS(ChatFormatting.BLUE.getColor(), Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE),
        REDSTONE(ChatFormatting.RED.getColor(), Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE),
        DIAMOND(ChatFormatting.AQUA.getColor(), Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE),
        QUARTZ(ChatFormatting.WHITE.getColor(), Blocks.NETHER_QUARTZ_ORE),
        EMERALD(ChatFormatting.GREEN.getColor(), Blocks.EMERALD_ORE),
        NETHERITE(10506797, Blocks.ANCIENT_DEBRIS);

        private final List<Block> blockTypes;
        private final TextColor textColor;

        OreType(int color, Block... blocks) {
            this.blockTypes = Arrays.asList(blocks);
            this.textColor = TextColor.fromRgb(Integer.parseInt("0x" + Integer.toHexString(color)));
        }

        public boolean is(BlockState state) {
            return blockTypes.contains(state.getBlock());
        }

        public OreType next() {
            return values()[(ordinal() + 1) % values().length];
        }
    }
}
