package com.chaosbuffalo.mkweapons.items.weapon.effects;

import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class UndeadDamage implements IWeaponEffect{
    private final float damageMultiplier;
    public UndeadDamage(float multiplier){
        this.damageMultiplier = multiplier;
    }

    @Override
    public float modifyDamageDealt(float damage, IMKMeleeWeapon weapon, ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target.isEntityUndead()){
            return damage * damageMultiplier;
        } else {
            return damage;
        }
    }
}
