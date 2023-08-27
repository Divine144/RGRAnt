package com.divinity.hmedia.rgrant.init;

import com.divinity.hmedia.rgrant.RGRAnt;
import com.divinity.hmedia.rgrant.item.*;
import dev._100media.hundredmediageckolib.item.animated.AnimatedItemProperties;
import dev._100media.hundredmediageckolib.item.animated.SimpleAnimatedItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RGRAnt.MODID);

    public static final RegistryObject<Item> MANDIBLES = ITEMS.register("mandibles", () -> new MandiblesItem(new AnimatedItemProperties().stacksTo(1)));

    public static final RegistryObject<Item> ACID_SPRAY = ITEMS.register("acid_spray", () -> new AcidSprayItem(new AnimatedItemProperties().stacksTo(1)));

    public static final RegistryObject<Item> BUG_SPRAY = ITEMS.register("bug_spray", () -> new BugSprayItem(new AnimatedItemProperties().stacksTo(1)));

    public static final RegistryObject<Item> LEAF_CUTTER_TOOLS = ITEMS.register("leaf_cutter_tools", () -> new Item(new AnimatedItemProperties().stacksTo(1)));
    public static final RegistryObject<Item> VENOMOUS_STING = ITEMS.register("venomous_sting", () -> new VenomousStingItem(new AnimatedItemProperties().stacksTo(1)));

    public static final RegistryObject<Item> MIND_CONTROL = ITEMS.register("mind_control", () -> new MindControlItem(new Item.Properties().stacksTo(1)));


    public static Item.Properties getItemProperties() {
        return new Item.Properties();
    }
}
