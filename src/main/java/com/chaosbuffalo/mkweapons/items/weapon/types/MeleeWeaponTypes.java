package com.chaosbuffalo.mkweapons.items.weapon.types;

import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.effects.melee.*;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
                1.5f, -2.4f, 0.5f, 0.05f, 0.0f, false,
            0.75f, 25f,
            new FuryStrikeMeleeWeaponEffect(5, .25));
    public static final MeleeWeaponType GREATSWORD_TYPE = new MeleeWeaponType(
            new ResourceLocation(MKWeapons.MODID, "greatsword"),
            2.0f, -3.0f, 0.75f, 0.05f, 1.0f, true,
            0.8f, 25f,
            new DoubleStrikeMeleeWeaponEffect(.2));
    public static final MeleeWeaponType KATANA_TYPE = new MeleeWeaponType(
            new ResourceLocation(MKWeapons.MODID, "katana"),
            1.5f, -2.2f, 1.0f, 0.10f, 0.0f, true,
            0.75f, 25f,
            new ComboStrikeMeleeWeaponEffect(5, .25));
    public static final MeleeWeaponType DAGGER_TYPE = new MeleeWeaponType(
            new ResourceLocation(MKWeapons.MODID, "dagger"),
            1.0f, -1.0f, 1.5f, 0.10f, -1.0f, false,
            0.5f, 20f,
            new BleedMeleeWeaponEffect(2.0f, 10, 4),
            new ComboStrikeMeleeWeaponEffect(3, .50));
    public static final MeleeWeaponType STAFF_TYPE = new MeleeWeaponType(
            new ResourceLocation(MKWeapons.MODID, "staff"),
            1.75f, -2.5f, 0.5f, 0.05f, 1.0f, true,
            0.85f, 30f,
            new StunMeleeWeaponEffect(.20, 2),
            new ComboStrikeMeleeWeaponEffect(5, .15));
    public static final MeleeWeaponType SPEAR_TYPE = new MeleeWeaponType(
            new ResourceLocation(MKWeapons.MODID, "spear"),
            1.25f, -2.0f, 0.75f, 0.05f, 2.0f, false,
            0.75f, 30f,
            new BleedMeleeWeaponEffect(1.5f, 5, 5),
            new FuryStrikeMeleeWeaponEffect(3, .4));
    public static final MeleeWeaponType WARHAMMER_TYPE = new MeleeWeaponType(
            new ResourceLocation(MKWeapons.MODID, "warhammer"),
            2.25f, -2.75f, 0.25f, 0.05f, 1.0f, true,
            0.8f, 25f,
            new UndeadDamageMeleeWeaponEffect(2.0f),
            new StunMeleeWeaponEffect(.1, 5));
    public static final MeleeWeaponType BATTLEAXE_TYPE = new MeleeWeaponType(
            new ResourceLocation(MKWeapons.MODID, "battleaxe"),
            2.5f, -3.2f, 0.75f, 0.10f, 0.0f, true,
            0.8f, 25f,
            new BleedMeleeWeaponEffect(2.0f, 2, 4));
    public static final MeleeWeaponType MACE_TYPE = new MeleeWeaponType(
            new ResourceLocation(MKWeapons.MODID, "mace"),
            1.75f, -2.1f, 0.25f, 0.05f, 0.0f, false,
            0.75f, 30f,
            new UndeadDamageMeleeWeaponEffect(1.5f),
            new DoubleStrikeMeleeWeaponEffect(.1));

    public static final Set<MeleeWeaponType> WITH_BLOCKING = new HashSet<>();

    static {
        WITH_BLOCKING.add(MeleeWeaponTypes.KATANA_TYPE);
        WITH_BLOCKING.add(MeleeWeaponTypes.GREATSWORD_TYPE);
        WITH_BLOCKING.add(MeleeWeaponTypes.BATTLEAXE_TYPE);
        WITH_BLOCKING.add(MeleeWeaponTypes.DAGGER_TYPE);
        WITH_BLOCKING.add(MeleeWeaponTypes.LONGSWORD_TYPE);
        WITH_BLOCKING.add(MeleeWeaponTypes.MACE_TYPE);
        WITH_BLOCKING.add(MeleeWeaponTypes.SPEAR_TYPE);
        WITH_BLOCKING.add(MeleeWeaponTypes.STAFF_TYPE);
        WITH_BLOCKING.add(MeleeWeaponTypes.WARHAMMER_TYPE);
    }


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
