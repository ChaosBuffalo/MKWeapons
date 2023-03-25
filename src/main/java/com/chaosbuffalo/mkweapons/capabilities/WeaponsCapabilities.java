package com.chaosbuffalo.mkweapons.capabilities;

import com.chaosbuffalo.mkweapons.MKWeapons;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class WeaponsCapabilities {

    public static ResourceLocation MK_ARROW_CAP_ID = new ResourceLocation(MKWeapons.MODID, "arrow_data");
    public static ResourceLocation MK_WEAPON_CAP_ID = new ResourceLocation(MKWeapons.MODID, "melee_weapon_data");
    public static ResourceLocation MK_ARMOR_CAP_ID = new ResourceLocation(MKWeapons.MODID, "armor_data");

    @CapabilityInject(IArrowData.class)
    public static final Capability<IArrowData> ARROW_DATA_CAPABILITY;

    @CapabilityInject(IWeaponData.class)
    public static final Capability<IWeaponData> WEAPON_DATA_CAPABILITY;

    @CapabilityInject(IArmorData.class)
    public static final Capability<IArmorData> ARMOR_DATA_CAPABILITY;

    static {
        ARROW_DATA_CAPABILITY = null;
        WEAPON_DATA_CAPABILITY = null;
        ARMOR_DATA_CAPABILITY = null;
    }

    public static void registerCapabilities() {
        CapabilityManager.INSTANCE.register(IArrowData.class, new ArrowDataHandler.Storage(), ArrowDataHandler::new);
        CapabilityManager.INSTANCE.register(IWeaponData.class, new WeaponDataHandler.Storage(), WeaponDataHandler::new);
        CapabilityManager.INSTANCE.register(IArmorData.class, new ArmorDataHandler.Storage(), ArmorDataHandler::new);
    }
}
