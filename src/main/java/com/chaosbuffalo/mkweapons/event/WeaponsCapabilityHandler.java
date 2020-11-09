package com.chaosbuffalo.mkweapons.event;

import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.capabilities.ArrowDataProvider;
import com.chaosbuffalo.mkweapons.capabilities.WeaponsCapabilities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
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
}

