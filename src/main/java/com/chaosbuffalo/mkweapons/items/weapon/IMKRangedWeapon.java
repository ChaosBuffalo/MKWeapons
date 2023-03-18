package com.chaosbuffalo.mkweapons.items.weapon;

import com.chaosbuffalo.mkweapons.items.effects.ranged.IRangedWeaponEffect;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public interface IMKRangedWeapon extends IMKWeapon {

    @Override
    List<IRangedWeaponEffect> getWeaponEffects(ItemStack item);

    void forEachRangedEffect(ItemStack itemStack, Consumer<IRangedWeaponEffect> consumer);
}
