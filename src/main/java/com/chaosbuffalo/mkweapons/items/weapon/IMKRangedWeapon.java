package com.chaosbuffalo.mkweapons.items.weapon;

import com.chaosbuffalo.mkweapons.items.weapon.effects.ranged.IRangedWeaponEffect;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface IMKRangedWeapon extends IMKWeapon {

    List<IRangedWeaponEffect> getWeaponEffects(ItemStack item);

    List<IRangedWeaponEffect> getWeaponEffects();


}
