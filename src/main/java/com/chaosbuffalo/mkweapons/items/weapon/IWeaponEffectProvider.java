package com.chaosbuffalo.mkweapons.items.weapon;

import com.chaosbuffalo.mkweapons.items.weapon.effects.IWeaponEffect;

import java.util.List;

public interface IWeaponEffectProvider {

    List<IWeaponEffect> getWeaponEffects();
}
