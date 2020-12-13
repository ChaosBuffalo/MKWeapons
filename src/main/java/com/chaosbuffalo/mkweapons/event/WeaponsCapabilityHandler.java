package com.chaosbuffalo.mkweapons.event;

import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.capabilities.ArrowDataProvider;
import com.chaosbuffalo.mkweapons.capabilities.WeaponDataProvider;
import com.chaosbuffalo.mkweapons.capabilities.WeaponsCapabilities;
import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import com.chaosbuffalo.mkweapons.items.weapon.IMKWeapon;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid= MKWeapons.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class WeaponsCapabilityHandler {

    @SubscribeEvent
    public static void attachEntityCapability(AttachCapabilitiesEvent<Entity> e) {
        if (e.getObject() instanceof AbstractArrowEntity) {
            e.addCapability(WeaponsCapabilities.MK_ARROW_CAP_ID,
                    new ArrowDataProvider((AbstractArrowEntity) e.getObject()));
        }
    }

    @SubscribeEvent
    public static void attachWeaponCapability(AttachCapabilitiesEvent<ItemStack> event){
        if (event.getObject().getItem() instanceof IMKWeapon){
            event.addCapability(WeaponsCapabilities.MK_WEAPON_CAP_ID,
                    new WeaponDataProvider(event.getObject()));
        }
    }
}

