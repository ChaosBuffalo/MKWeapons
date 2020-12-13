package com.chaosbuffalo.mkweapons.items.weapon.types;

import com.chaosbuffalo.mkweapons.items.weapon.effects.melee.IMeleeWeaponEffect;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.item.IItemTier;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public interface IMeleeWeaponType {

    float getDamageMultiplier();

    float getAttackSpeed();

    float getCritMultiplier();

    float getCritChance();

    float getReach();

    boolean isTwoHanded();

    List<IMeleeWeaponEffect> getWeaponEffects();

    <D> D serialize(DynamicOps<D> ops);

    <D> void deserialize(Dynamic<D> dynamic);

    ResourceLocation getName();

    default float getDamageForTier(IItemTier tier){
        return (tier.getAttackDamage() + 3) * getDamageMultiplier();
    }
}
