package com.chaosbuffalo.mkweapons.items.weapon.types;

import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.weapon.effects.melee.*;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;

public class MeleeWeaponTypes {

    public static final HashMap<ResourceLocation, IMeleeWeaponType> WEAPON_TYPES = new HashMap<net.minecraft.util.ResourceLocation, IMeleeWeaponType>();

    public static void addWeaponType(IMeleeWeaponType weaponType){
        WEAPON_TYPES.put(weaponType.getName(), weaponType);
    }

    @Nullable
    public static IMeleeWeaponType getWeaponType(ResourceLocation name){
        return WEAPON_TYPES.get(name);
    }

    public static final MeleeWeaponType LONGSWORD_TYPE = new MeleeWeaponType(
            new ResourceLocation(MKWeapons.MODID, "longsword"),
                1.5f, -2.4f, 1.5f, 0.05f, 0.0f, false,
            new FuryStrikeMeleeWeaponEffect(5, .25));
    public static final MeleeWeaponType GREATSWORD_TYPE = new MeleeWeaponType(
            new ResourceLocation(MKWeapons.MODID, "greatsword"),
            2.0f, -3.2f, 1.75f, 0.05f, 1.0f, true,
            new DoubleStrikeMeleeWeaponEffect(.2));
    public static final MeleeWeaponType KATANA_TYPE = new MeleeWeaponType(
            new ResourceLocation(MKWeapons.MODID, "katana"),
            1.5f, -2.2f, 2.0f, 0.10f, 0.0f, true,
            new ComboStrikeMeleeWeaponEffect(5, .25));
    public static final MeleeWeaponType DAGGER_TYPE = new MeleeWeaponType(
            new ResourceLocation(MKWeapons.MODID, "dagger"),
            1.0f, -1.0f, 2.5f, 0.10f, -1.0f, false,
            new BleedMeleeWeaponEffect(2.0f, 10, 4),
            new ComboStrikeMeleeWeaponEffect(3, .50));
    public static final MeleeWeaponType STAFF_TYPE = new MeleeWeaponType(
            new ResourceLocation(MKWeapons.MODID, "staff"),
            1.0f, -2.5f, 1.5f, 0.05f, 1.0f, true,
            new StunMeleeWeaponEffect(.20, 2),
            new ComboStrikeMeleeWeaponEffect(5, .15));
    public static final MeleeWeaponType SPEAR_TYPE = new MeleeWeaponType(
            new ResourceLocation(MKWeapons.MODID, "spear"),
            1.25f, -2.0f, 1.75f, 0.05f, 2.0f, false,
            new BleedMeleeWeaponEffect(1.5f, 5, 5),
            new FuryStrikeMeleeWeaponEffect(3, .4));
    public static final MeleeWeaponType WARHAMMER_TYPE = new MeleeWeaponType(
            new ResourceLocation(MKWeapons.MODID, "warhammer"),
            2.25f, -2.75f, 1.25f, 0.05f, 1.0f, true,
            new UndeadDamageMeleeWeaponEffect(2.0f),
            new StunMeleeWeaponEffect(.1, 5));
    public static final MeleeWeaponType BATTLEAXE_TYPE = new MeleeWeaponType(
            new ResourceLocation(MKWeapons.MODID, "battleaxe"),
            2.5f, -3.5f, 1.75f, 0.10f, 0.0f, true,
            new BleedMeleeWeaponEffect(2.0f, 2, 4));
    public static final MeleeWeaponType MACE_TYPE = new MeleeWeaponType(
            new ResourceLocation(MKWeapons.MODID, "mace"),
            1.75f, -2.1f, 1.25f, 0.05f, 0.0f, false,
            new UndeadDamageMeleeWeaponEffect(1.5f),
            new DoubleStrikeMeleeWeaponEffect(.1));

    public static void registerWeaponTypes(){
        addWeaponType(LONGSWORD_TYPE);
        addWeaponType(GREATSWORD_TYPE);
        addWeaponType(KATANA_TYPE);
        addWeaponType(DAGGER_TYPE);
        addWeaponType(STAFF_TYPE);
        addWeaponType(SPEAR_TYPE);
        addWeaponType(WARHAMMER_TYPE);
        addWeaponType(BATTLEAXE_TYPE);
        addWeaponType(MACE_TYPE);
    }
}
