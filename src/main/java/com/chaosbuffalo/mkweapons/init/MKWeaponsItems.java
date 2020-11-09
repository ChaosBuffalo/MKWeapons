package com.chaosbuffalo.mkweapons.init;


import com.chaosbuffalo.mkcore.GameConstants;
import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.MKBow;
import com.chaosbuffalo.mkweapons.items.MKMeleeWeapon;
import com.chaosbuffalo.mkweapons.items.weapon.effects.ranged.RapidFireRangedWeaponEffect;
import com.chaosbuffalo.mkweapons.items.weapon.tier.MKTier;
import com.chaosbuffalo.mkweapons.items.weapon.types.IMeleeWeaponType;
import com.chaosbuffalo.mkweapons.items.weapon.types.MeleeWeaponTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemTier;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ObjectHolder(MKWeapons.MODID)
@Mod.EventBusSubscriber(modid = MKWeapons.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MKWeaponsItems {

    public static List<MKMeleeWeapon> WEAPONS = new ArrayList<>();

    public static List<MKBow> BOWS = new ArrayList<>();

    @ObjectHolder("haft")
    public static Item Haft;

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> evt){
        Set<Tuple<String, MKTier>> materials = new HashSet<>();
        materials.add(new Tuple<>("iron", new MKTier(ItemTier.IRON, "iron", Tags.Items.INGOTS_IRON)));
        materials.add(new Tuple<>("wood", new MKTier(ItemTier.WOOD, "wood", ItemTags.PLANKS)));
        materials.add(new Tuple<>("diamond", new MKTier(ItemTier.DIAMOND, "diamond", Tags.Items.GEMS_DIAMOND)));
        materials.add(new Tuple<>("gold", new MKTier(ItemTier.GOLD, "gold", Tags.Items.INGOTS_GOLD)));
        materials.add(new Tuple<>("stone", new MKTier(ItemTier.STONE, "stone", Tags.Items.COBBLESTONE)));
        for (Tuple<String, MKTier> mat : materials){
            for (IMeleeWeaponType weaponType : MeleeWeaponTypes.WEAPON_TYPES.values()){
                MKMeleeWeapon weapon = new MKMeleeWeapon(new ResourceLocation(MKWeapons.MODID,
                        String.format("%s_%s", weaponType.getName(), mat.getA())), mat.getB(), weaponType,
                        (new Item.Properties()).group(ItemGroup.COMBAT));
                WEAPONS.add(weapon);
                evt.getRegistry().register(weapon);
            }
            MKBow bow = new MKBow(new ResourceLocation(MKWeapons.MODID, String.format("longbow_%s", mat.getA())),
                    new Item.Properties().maxDamage(mat.getB().getMaxUses() * 3).group(ItemGroup.COMBAT), mat.getB(),
                    GameConstants.TICKS_PER_SECOND * 2.5f, 4.0f,
                    new RapidFireRangedWeaponEffect(7, .10f));
            BOWS.add(bow);
            evt.getRegistry().register(bow);
        }
        Item haft = new Item(new Item.Properties().group(ItemGroup.MATERIALS));
        haft.setRegistryName(MKWeapons.MODID, "haft");
        evt.getRegistry().register(haft);
    }
}
