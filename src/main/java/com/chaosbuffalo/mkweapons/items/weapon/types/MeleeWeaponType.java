package com.chaosbuffalo.mkweapons.items.weapon.types;

import com.chaosbuffalo.mkweapons.items.weapon.effects.IWeaponEffect;

import java.util.Arrays;
import java.util.List;

public class MeleeWeaponType implements IMeleeWeaponType {
    private final float damageMultiplier;
    private final float attackSpeed;
    private final float critMultiplier;
    private final float critChance;
    private final float reach;
    private final String name;
    private final List<IWeaponEffect> effects;
    private final boolean isTwoHanded;

    public MeleeWeaponType(String name, float damageMultiplier, float attackSpeed,
                           float critMultiplier, float critChance, float reach, boolean isTwoHanded,
                           IWeaponEffect... effects){
        this.damageMultiplier = damageMultiplier;
        this.name = name;
        this.attackSpeed = attackSpeed;
        this.critMultiplier = critMultiplier;
        this.critChance = critChance;
        this.reach = reach;
        this.effects = Arrays.asList(effects);
        this.isTwoHanded = isTwoHanded;
    }

    @Override
    public boolean isTwoHanded() {
        return isTwoHanded;
    }

    public String getTranslationName(){
        return String.format("mkweapon.melee.type.%s", getName());
    }

    @Override
    public List<IWeaponEffect> getWeaponEffects() {
        return effects;
    }

    @Override
    public float getDamageMultiplier() {
        return damageMultiplier;
    }

    @Override
    public float getAttackSpeed() {
        return attackSpeed;
    }

    @Override
    public float getCritMultiplier() {
        return critMultiplier;
    }

    @Override
    public float getCritChance() {
        return critChance;
    }

    @Override
    public float getReach() {
        return reach;
    }

    @Override
    public String getName() {
        return name;
    }
}
