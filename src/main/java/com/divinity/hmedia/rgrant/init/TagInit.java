package com.divinity.hmedia.rgrant.init;

import com.divinity.hmedia.rgrant.RGRAnt;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class TagInit {

    //example
    public static TagKey<Item> CORAL = itemTag("coral");
    public static TagKey<Item> HORSE_ARMOR = itemTag("horse_armor");


    public static void init() {
        Minecraft.getInstance().getSoundManager().stop();
    }

//    public static TagKey<Morph> morphTag(String path) {
//        return HMMMorphInit.getRegistry().tags().createTagKey(new ResourceLocation(HundredDaysStory.MODID, path));
//    }

    public static TagKey<Block> blockTag(String path) {
        return BlockTags.create(new ResourceLocation(RGRAnt.MODID, path));
    }

    public static TagKey<Item> itemTag(String path) {
        return ItemTags.create(new ResourceLocation(RGRAnt.MODID, path));
    }


}
