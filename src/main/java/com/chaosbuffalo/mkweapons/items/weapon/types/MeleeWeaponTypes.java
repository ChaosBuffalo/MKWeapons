package com.chaosbuffalo.mkweapons.items.weapon.types;

import com.chaosbuffalo.mkweapons.items.weapon.effects.*;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;

public class MeleeWeaponTypes {

    public static final HashMap<String, IMeleeWeaponType> WEAPON_TYPES = new HashMap<String, IMeleeWeaponType>();

    public static void addWeaponType(IMeleeWeaponType weaponType){
        WEAPON_TYPES.put(weaponType.getName(), weaponType);
    }

    @Nullable
    public static IMeleeWeaponType getWeaponType(ResourceLocation name){
        return WEAPON_TYPES.get(name);
    }

    public static final MeleeWeaponType LONGSWORD_TYPE = new MeleeWeaponType("longsword",
                1.5f, -2.4f, 1.5f, 0.05f, 0.0f, false,
            new FuryStrikeWeaponEffect(5, .25));
    public static final MeleeWeaponType GREATSWORD_TYPE = new MeleeWeaponType("greatsword",
            2.0f, -3.2f, 1.75f, 0.05f, 1.0f, true,
            new DoubleStrikeWeaponEffect(.2));
    public static final MeleeWeaponType KATANA_TYPE = new MeleeWeaponType("katana",
            1.5f, -2.2f, 2.0f, 0.10f, 0.0f, true,
            new ComboStrikeWeaponEffect(5, .25));
    public static final MeleeWeaponType DAGGER_TYPE = new MeleeWeaponType("dagger",
            1.0f, -1.0f, 2.5f, 0.10f, -1.0f, false,
            new BleedWeaponEffect(2.0f, 10, 4),
            new ComboStrikeWeaponEffect(3, .50));
    public static final MeleeWeaponType STAFF_TYPE = new MeleeWeaponType("staff",
            1.0f, -2.5f, 1.5f, 0.05f, 1.0f, true,
            new StunWeaponEffect(.20, 2),
            new ComboStrikeWeaponEffect(5, .15));
    public static final MeleeWeaponType SPEAR_TYPE = new MeleeWeaponType("spear",
            1.25f, -2.0f, 1.75f, 0.05f, 2.0f, false,
            new BleedWeaponEffect(1.5f, 5, 5),
            new FuryStrikeWeaponEffect(3, .4));
    public static final MeleeWeaponType WARHAMMER_TYPE = new MeleeWeaponType("warhammer",
            2.25f, -2.75f, 1.25f, 0.05f, 1.0f, true,
            new UndeadDamageWeaponEffect(2.0f),
            new StunWeaponEffect(.1, 5));
    public static final MeleeWeaponType BATTLEAXE_TYPE = new MeleeWeaponType("battleaxe",
            2.5f, -3.5f, 1.75f, 0.10f, 0.0f, true,
            new BleedWeaponEffect(4.0f, 3, 4));
    public static final MeleeWeaponType MACE_TYPE = new MeleeWeaponType("mace",
            1.75f, -2.1f, 1.25f, 0.05f, 0.0f, false,
            new UndeadDamageWeaponEffect(1.5f),
            new DoubleStrikeWeaponEffect(.1));

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
