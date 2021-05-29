package com.chaosbuffalo.mkweapons.items.weapon;

import com.chaosbuffalo.mkcore.core.IMKAbilityProvider;
import com.chaosbuffalo.mkweapons.items.weapon.effects.IWeaponEffect;
import com.chaosbuffalo.mkweapons.items.weapon.effects.ranged.IRangedWeaponEffect;
import com.chaosbuffalo.mkweapons.items.weapon.tier.MKTier;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface IMKWeapon extends IMKAbilityProvider {

    MKTier getMKTier();

    default void reload(){

    }

    List<? extends IWeaponEffect> getWeaponEffects(ItemStack item);

    List<? extends IWeaponEffect> getWeaponEffects();
}
