package com.chaosbuffalo.mkweapons.init;


import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.MKMeleeWeapon;
import com.chaosbuffalo.mkweapons.items.weapon.tier.MKTier;
import com.chaosbuffalo.mkweapons.items.weapon.types.IMeleeWeaponType;
import com.chaosbuffalo.mkweapons.items.weapon.types.MeleeWeaponTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemTier;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid = MKWeapons.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MKWeaponsItems {

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> evt){

        Set<Tuple<String, MKTier>> materials = new HashSet<>();
        materials.add(new Tuple<>("iron", new MKTier(ItemTier.IRON)));
        for (Tuple<String, MKTier> mat : materials){
            for (IMeleeWeaponType weaponType : MeleeWeaponTypes.WEAPON_TYPES.values()){
                MKMeleeWeapon weapon = new MKMeleeWeapon(new ResourceLocation(MKWeapons.MODID,
                        String.format("%s_%s", weaponType.getName(), mat.getA())), mat.getB(), weaponType,
                        (new Item.Properties()).group(ItemGroup.COMBAT));
                evt.getRegistry().register(weapon);
            }
        }

    }
}
