package com.divinity.hmedia.rgrant.datagen;

import com.divinity.hmedia.rgrant.RGRAnt;
import com.divinity.hmedia.rgrant.init.TagInit;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ModTagProvider {

    public static class ModItemTags extends TagsProvider<Item>{

        public ModItemTags(PackOutput p_256596_, CompletableFuture<HolderLookup.Provider> p_256513_, @Nullable ExistingFileHelper existingFileHelper) {
            super(p_256596_, Registries.ITEM, p_256513_, RGRAnt.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider pProvider) {
            tag(TagInit.CORAL)
                    .add(asResourceKey(Items.TUBE_CORAL), asResourceKey(Items.BRAIN_CORAL), asResourceKey(Items.BUBBLE_CORAL), asResourceKey(Items.FIRE_CORAL), asResourceKey(Items.HORN_CORAL),
                            asResourceKey(Items.DEAD_TUBE_CORAL), asResourceKey(Items.DEAD_BRAIN_CORAL), asResourceKey(Items.DEAD_BUBBLE_CORAL), asResourceKey(Items.DEAD_FIRE_CORAL), asResourceKey(Items.DEAD_HORN_CORAL));
            tag(TagInit.HORSE_ARMOR)
                    .add(asResourceKey(Items.LEATHER_HORSE_ARMOR), asResourceKey(Items.GOLDEN_HORSE_ARMOR), asResourceKey(Items.IRON_HORSE_ARMOR), asResourceKey(Items.DIAMOND_HORSE_ARMOR));
        }

        private static ResourceKey<Item> asResourceKey(Item item) {
            return ForgeRegistries.ITEMS.getResourceKey(item).orElse(null);
        }

        public void populateTag(TagKey<Item> tag, Supplier<Item>... items){
            for (Supplier<Item> item : items) {
                tag(tag).add(ForgeRegistries.ITEMS.getResourceKey(item.get()).get());
            }
        }
    }

    public static class ModBlockTags extends TagsProvider<Block>{

        public ModBlockTags(PackOutput pGenerator, CompletableFuture<HolderLookup.Provider> p_256513_, @Nullable ExistingFileHelper existingFileHelper) {
            super(pGenerator, Registries.BLOCK, p_256513_, RGRAnt.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider pProvider) {

        }
        public  <T extends Block>void populateTag(TagKey<Block> tag, Supplier<?>... items){
            for (Supplier<?> item : items) {
                tag(tag).add(ForgeRegistries.BLOCKS.getResourceKey((Block)item.get()).get());
            }
        }
    }
}
