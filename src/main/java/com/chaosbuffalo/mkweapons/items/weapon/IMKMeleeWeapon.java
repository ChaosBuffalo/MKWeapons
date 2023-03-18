package com.chaosbuffalo.mkweapons.items.weapon;

import com.chaosbuffalo.mkweapons.items.effects.melee.IMeleeWeaponEffect;
import com.chaosbuffalo.mkweapons.items.weapon.types.IMeleeWeaponType;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public interface IMKMeleeWeapon extends IMKWeapon {

    IMeleeWeaponType getWeaponType();

    default float getDamageForTier() {
        return getWeaponType().getDamageForTier(getMKTier());
    }

    default boolean allowSweep() {
        return false;
    }

    @Override
    List<IMeleeWeaponEffect> getWeaponEffects(ItemStack item);

    void forEachMeleeEffect(ItemStack itemStack, Consumer<IMeleeWeaponEffect> consumer);
}
