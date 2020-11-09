package com.chaosbuffalo.mkweapons.items.weapon;

import com.chaosbuffalo.mkweapons.items.weapon.effects.melee.IMeleeWeaponEffect;
import com.chaosbuffalo.mkweapons.items.weapon.tier.MKTier;
import com.chaosbuffalo.mkweapons.items.weapon.types.IMeleeWeaponType;

import java.util.List;

public interface IMKMeleeWeapon {

    IMeleeWeaponType getWeaponType();

    MKTier getMKTier();

    default float getDamageForTier() {
        return getWeaponType().getDamageForTier(getMKTier());
    }

    List<IMeleeWeaponEffect> getWeaponEffects();

}
