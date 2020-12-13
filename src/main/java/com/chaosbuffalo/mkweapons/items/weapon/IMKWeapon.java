package com.chaosbuffalo.mkweapons.items.weapon;

import com.chaosbuffalo.mkcore.core.IMKAbilityProvider;
import com.chaosbuffalo.mkweapons.items.weapon.tier.MKTier;

public interface IMKWeapon extends IMKAbilityProvider {

    MKTier getMKTier();

    default void reload(){

    }
}
