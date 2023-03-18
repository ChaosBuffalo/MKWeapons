package com.chaosbuffalo.mkweapons.capabilities;

import com.chaosbuffalo.mkweapons.items.effects.melee.IMeleeWeaponEffect;

import java.util.List;
import java.util.function.Consumer;

public interface IMeleeWeaponData extends IWeaponData {

    boolean hasMeleeWeaponEffects();

    List<IMeleeWeaponEffect> getMeleeEffects();

    void addMeleeWeaponEffect(IMeleeWeaponEffect weaponEffect);

    void removeMeleeWeaponEffect(int index);

    void forEachMeleeEffect(Consumer<? super IMeleeWeaponEffect> consumer);
}
