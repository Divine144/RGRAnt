package com.divinity.hmedia.rgrant.utils;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public class AntUtils {

    public static boolean hasItemEitherHands(Player player, Item item) {
        return player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == item || player.getItemInHand(InteractionHand.OFF_HAND).getItem() == item;
    }
}
