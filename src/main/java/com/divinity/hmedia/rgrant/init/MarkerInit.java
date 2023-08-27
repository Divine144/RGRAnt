package com.divinity.hmedia.rgrant.init;

import com.divinity.hmedia.rgrant.RGRAnt;
import dev._100media.hundredmediaabilities.HundredMediaAbilitiesMod;
import dev._100media.hundredmediaabilities.marker.Marker;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MarkerInit {
    public static final DeferredRegister<Marker> MARKERS = DeferredRegister.create(new ResourceLocation(HundredMediaAbilitiesMod.MODID, "markers"), RGRAnt.MODID);
    public static final RegistryObject<Marker> MANDIBLES_MARKER = MARKERS.register("mandibles_marker", Marker::new);


}
