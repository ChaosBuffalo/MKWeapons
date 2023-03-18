package com.chaosbuffalo.mkweapons.capabilities;

import com.chaosbuffalo.mkweapons.items.effects.ranged.IRangedWeaponEffect;

import java.util.function.Consumer;

public interface IRangedWeaponData extends IWeaponData {

    boolean hasRangedWeaponEffects();

    void addRangedWeaponEffect(IRangedWeaponEffect weaponEffect);

    void removeRangedWeaponEffect(int index);

    void forEachRangedEffect(Consumer<? super IRangedWeaponEffect> consumer);
}
