package com.chaosbuffalo.mkweapons.items.weapon.effects;

import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public interface IWeaponEffect {

    default void onHit(IMKMeleeWeapon tier, ItemStack stack,
                      LivingEntity target, LivingEntity attacker) { }

    default float modifyDamageDealt(float damage, IMKMeleeWeapon weapon, ItemStack stack,
                                    LivingEntity target, LivingEntity attacker){
        return damage;
    }
}
