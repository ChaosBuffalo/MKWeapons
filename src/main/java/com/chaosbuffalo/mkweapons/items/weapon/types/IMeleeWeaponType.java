package com.chaosbuffalo.mkweapons.items.weapon.types;

import com.chaosbuffalo.mkweapons.items.weapon.effects.melee.IMeleeWeaponEffect;
import net.minecraft.item.IItemTier;

import java.util.List;

public interface IMeleeWeaponType {

    float getDamageMultiplier();

    float getAttackSpeed();

    float getCritMultiplier();

    float getCritChance();

    float getReach();

    boolean isTwoHanded();

    List<IMeleeWeaponEffect> getWeaponEffects();

    String getName();

    default float getDamageForTier(IItemTier tier){
        return (tier.getAttackDamage() + 3) * getDamageMultiplier();
    }
}
