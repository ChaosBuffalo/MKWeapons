package com.chaosbuffalo.mkweapons.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MKWeaponsGenerator {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();
        if (event.includeServer()) {
            // recipes here
            gen.addProvider(new MKWeaponRecipeProvider(gen));
            gen.addProvider(new MKWeaponTypesProvider(gen));
            gen.addProvider(new LootTierProvider(gen));
        }
        if (event.includeClient()) {
            gen.addProvider(new MKWeaponModelProvider(gen, helper));
        }
    }
}