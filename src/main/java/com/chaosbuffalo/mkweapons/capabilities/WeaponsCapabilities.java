package com.chaosbuffalo.mkweapons.capabilities;

import com.chaosbuffalo.mkweapons.MKWeapons;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class WeaponsCapabilities {

    public static ResourceLocation MK_ARROW_CAP_ID = new ResourceLocation(MKWeapons.MODID, "arrow_data");

    @CapabilityInject(IArrowData.class)
    public static final Capability<IArrowData> ARROW_DATA_CAPABILITY;

    static {
        ARROW_DATA_CAPABILITY = null;
    }

    public static void registerCapabilities() {
        CapabilityManager.INSTANCE.register(IArrowData.class, new ArrowDataHandler.Storage(), ArrowDataHandler::new);
    }
}