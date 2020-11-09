package com.chaosbuffalo.mkweapons.items.weapon;

import com.chaosbuffalo.mkweapons.items.weapon.effects.ranged.IRangedWeaponEffect;
import com.chaosbuffalo.mkweapons.items.weapon.tier.MKTier;

import java.util.List;

public interface IMKBow {

    List<IRangedWeaponEffect> getWeaponEffects();

    MKTier getMKTier();
}
