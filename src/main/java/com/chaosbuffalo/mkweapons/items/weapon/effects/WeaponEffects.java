package com.chaosbuffalo.mkweapons.items.weapon.effects;

import com.chaosbuffalo.mkweapons.items.weapon.effects.melee.*;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class WeaponEffects {

    public static final Map<ResourceLocation, Supplier<IWeaponEffect>> WEAPON_EFFECT_DESERIALIZERS =
            new HashMap<>();

    public static void addWeaponEffectDeserializer(ResourceLocation type,
                                                   Supplier<IWeaponEffect> deserializer){
        WEAPON_EFFECT_DESERIALIZERS.put(type, deserializer);
    }

    static {
        addWeaponEffectDeserializer(BleedMeleeWeaponEffect.NAME, BleedMeleeWeaponEffect::new);
        addWeaponEffectDeserializer(ComboStrikeMeleeWeaponEffect.NAME, ComboStrikeMeleeWeaponEffect::new);
        addWeaponEffectDeserializer(DoubleStrikeMeleeWeaponEffect.NAME, DoubleStrikeMeleeWeaponEffect::new);
        addWeaponEffectDeserializer(FuryStrikeMeleeWeaponEffect.NAME, FuryStrikeMeleeWeaponEffect::new);
        addWeaponEffectDeserializer(StunMeleeWeaponEffect.NAME, StunMeleeWeaponEffect::new);
        addWeaponEffectDeserializer(UndeadDamageMeleeWeaponEffect.NAME, UndeadDamageMeleeWeaponEffect::new);
    }

    public static <D> IWeaponEffect deserializeEffect(Dynamic<D> dynamic){
        ResourceLocation type = BaseWeaponEffect.readType(dynamic);
        IWeaponEffect weaponEffect = WEAPON_EFFECT_DESERIALIZERS.get(type).get();
        if (weaponEffect != null){
            weaponEffect.deserialize(dynamic);
        }
        return weaponEffect;
    }
}
