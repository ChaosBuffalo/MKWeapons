package com.chaosbuffalo.mkweapons.items.weapon.types;

import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.weapon.effects.IWeaponEffect;
import com.chaosbuffalo.mkweapons.items.weapon.effects.WeaponEffects;
import com.chaosbuffalo.mkweapons.items.weapon.effects.melee.IMeleeWeaponEffect;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MeleeWeaponType implements IMeleeWeaponType {
    private float damageMultiplier;
    private float attackSpeed;
    private float critMultiplier;
    private float critChance;
    private float reach;
    private final ResourceLocation name;
    private final List<IMeleeWeaponEffect> effects;
    private boolean isTwoHanded;

    public MeleeWeaponType(ResourceLocation name, float damageMultiplier, float attackSpeed,
                           float critMultiplier, float critChance, float reach, boolean isTwoHanded,
                           IMeleeWeaponEffect... effects){
        this.damageMultiplier = damageMultiplier;
        this.name = name;
        this.attackSpeed = attackSpeed;
        this.critMultiplier = critMultiplier;
        this.critChance = critChance;
        this.reach = reach;
        this.effects = new ArrayList<>();
        this.effects.addAll(Arrays.asList(effects));
        this.isTwoHanded = isTwoHanded;
    }

    @Override
    public boolean isTwoHanded() {
        return isTwoHanded;
    }

    public String getTranslationName(){
        return String.format("mkweapon.melee.type.%s.%s", getName().getNamespace(), getName().getPath());
    }

    @Override
    public List<IMeleeWeaponEffect> getWeaponEffects() {
        return effects;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops) {
        return ops.mergeToMap(ops.createMap(ImmutableMap.of(
                ops.createString("damageMultiplier"), ops.createFloat(getDamageMultiplier()),
                ops.createString("attackSpeed"), ops.createFloat(getAttackSpeed()),
                ops.createString("reach"), ops.createFloat(getReach()),
                ops.createString("critMultiplier"), ops.createFloat(getCritMultiplier()),
                ops.createString("critChance"), ops.createFloat(getCritChance())
                )),
                ImmutableMap.of(
                        ops.createString("isTwoHanded"), ops.createBoolean(isTwoHanded()),
                        ops.createString("effects"),
                        ops.createList(getWeaponEffects().stream().map(effect -> effect.serialize(ops)))
                )).result().orElse(ops.createMap(ImmutableMap.of()));
    }

    @Override
    public <D> void deserialize(Dynamic<D> dynamic) {
        damageMultiplier = dynamic.get("damageMultiplier").asFloat(1.0f);
        attackSpeed = dynamic.get("attackSpeed").asFloat(-2.4f);
        reach = dynamic.get("reach").asFloat(0f);
        critChance = dynamic.get("critChance").asFloat(0.05f);
        critMultiplier = dynamic.get("critMultiplier").asFloat(1.5f);
        isTwoHanded = dynamic.get("isTwoHanded").asBoolean(false);
        List<IMeleeWeaponEffect> deserializedEffects = dynamic.get("effects").asList(d -> {
            IWeaponEffect effect = WeaponEffects.deserializeEffect(d);
            if (effect instanceof IMeleeWeaponEffect){
                return (IMeleeWeaponEffect) effect;
            } else {
                MKWeapons.LOGGER.error("Failed to deserialize {} not a melee effect type for {}",
                        effect.getType(), getName());
                return null;
            }
        });
        effects.clear();
        for (IMeleeWeaponEffect effect : deserializedEffects){
            if (effect != null){
                effects.add(effect);
            }
        }
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
    public ResourceLocation getName() {
        return name;
    }
}
