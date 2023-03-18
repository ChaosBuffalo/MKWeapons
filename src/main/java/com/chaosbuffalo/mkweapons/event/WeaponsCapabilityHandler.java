package com.chaosbuffalo.mkweapons.event;

import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.capabilities.*;
import com.chaosbuffalo.mkweapons.items.armor.MKArmorItem;
import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import com.chaosbuffalo.mkweapons.items.weapon.IMKRangedWeapon;
import com.chaosbuffalo.mkweapons.items.weapon.IMKWeapon;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MKWeapons.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WeaponsCapabilityHandler {

    @SubscribeEvent
    public static void attachEntityCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof AbstractArrowEntity) {
            IArrowData data = new ArrowDataHandler((AbstractArrowEntity) event.getObject());
            ICapabilityProvider prov = CapabilityProvider.of(data, WeaponsCapabilities.ARROW_DATA_CAPABILITY);
            event.addCapability(WeaponsCapabilities.MK_ARROW_CAP_ID, prov);
        }
    }

    @SubscribeEvent
    public static void attachWeaponCapability(AttachCapabilitiesEvent<ItemStack> event) {
        if (event.getObject().getItem() instanceof IMKWeapon) {
            if (event.getObject().getItem() instanceof IMKMeleeWeapon) {
                IMeleeWeaponData data = new WeaponDataHandler.Melee(event.getObject());
                ICapabilityProvider prov = CapabilityProvider.of(data,
                        WeaponsCapabilities.MELEE_WEAPON_DATA_CAPABILITY, WeaponsCapabilities.WEAPON_DATA_CAPABILITY);
                event.addCapability(WeaponsCapabilities.MK_WEAPON_CAP_ID, prov);
            } else if (event.getObject().getItem() instanceof IMKRangedWeapon) {
                IRangedWeaponData data = new WeaponDataHandler.Ranged(event.getObject());
                ICapabilityProvider prov = CapabilityProvider.of(data,
                        WeaponsCapabilities.RANGED_WEAPON_DATA_CAPABILITY, WeaponsCapabilities.WEAPON_DATA_CAPABILITY);
                event.addCapability(WeaponsCapabilities.MK_WEAPON_CAP_ID, prov);
            }
        }
        if (event.getObject().getItem() instanceof MKArmorItem) {
            IArmorData data = new ArmorDataHandler(event.getObject());
            ICapabilityProvider prov = CapabilityProvider.of(data, WeaponsCapabilities.ARMOR_DATA_CAPABILITY);
            event.addCapability(WeaponsCapabilities.MK_ARMOR_CAP_ID, prov);
        }
    }
}
