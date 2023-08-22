package com.divinity.hmedia.rgrant.init;

import com.divinity.hmedia.rgrant.RGRAnt;
import dev._100media.hundredmediageckolib.item.animated.AnimatedItemProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RGRAnt.MODID);

    public static final RegistryObject<Item> LEAF_CUTTER_TOOLS = ITEMS.register("leaf_cutter_tools", () -> new CoinCannonItem(new AnimatedItemProperties().stacksTo(1)));
    public static final RegistryObject<Item> VENOMOUS_STING = ITEMS.register("venomous_sting", () -> new CoinCannonItem(new AnimatedItemProperties().stacksTo(1)));



    public static Item.Properties getItemProperties() {
        return new Item.Properties();
    }
}
