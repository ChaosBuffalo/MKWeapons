package com.chaosbuffalo.mkweapons.items.weapon;

import com.chaosbuffalo.mkweapons.items.weapon.tier.MKTier;
import com.chaosbuffalo.mkweapons.items.weapon.types.IMeleeWeaponType;

public interface IMKMeleeWeapon extends IWeaponEffectProvider {

    IMeleeWeaponType getWeaponType();

    MKTier getMKTier();

    default float getDamageForTier() {
        return getWeaponType().getDamageForTier(getMKTier());
    }
}
