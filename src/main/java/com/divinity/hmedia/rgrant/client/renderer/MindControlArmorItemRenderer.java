package com.divinity.hmedia.rgrant.client.renderer;

import com.divinity.hmedia.rgrant.RGRAnt;
import com.divinity.hmedia.rgrant.item.MindControlArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class MindControlArmorItemRenderer extends GeoArmorRenderer<MindControlArmorItem> {

    public MindControlArmorItemRenderer() {
        super(new DefaultedItemGeoModel<>(new ResourceLocation(RGRAnt.MODID, "armor/mind_control_armor")));
        this.body = null;
        this.leftArm = null;
        this.rightArm = null;
        this.leftLeg = null;
        this.rightLeg = null;
    }
}

