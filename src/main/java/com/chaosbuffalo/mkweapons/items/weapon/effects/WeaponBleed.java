package com.chaosbuffalo.mkweapons.items.weapon.effects;

import com.chaosbuffalo.mkcore.GameConstants;
import com.chaosbuffalo.mkcore.effects.SpellCast;
import com.chaosbuffalo.mkweapons.effects.BleedEffect;
import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import com.chaosbuffalo.mkweapons.items.weapon.types.IMeleeWeaponType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;

public class WeaponBleed implements IWeaponEffect {

    private final float damageMultiplier;
    private final int maxStacks;
    private final int durationSeconds;

    public WeaponBleed(float damageMultiplier, int maxStacks, int durationSeconds){
        this.damageMultiplier = damageMultiplier;
        this.maxStacks = maxStacks;
        this.durationSeconds = durationSeconds;
    }

    @Override
    public void onHit(IMKMeleeWeapon weapon, ItemStack stack,
                      LivingEntity target, LivingEntity attacker) {
        EffectInstance effect = target.getActivePotionEffect(BleedEffect.INSTANCE);
        float damagePerSecond = damageMultiplier * weapon.getDamageForTier() / durationSeconds;
        SpellCast cast = BleedEffect.Create(attacker, damagePerSecond, damagePerSecond).setTarget(target);
        EffectInstance newEffect;
        if (effect == null){
           newEffect = cast.toPotionEffect(GameConstants.TICKS_PER_SECOND * durationSeconds, 0);
        } else {
            int amplifier = Math.min(effect.getAmplifier() + 1, maxStacks);
            newEffect = cast.toPotionEffect(GameConstants.TICKS_PER_SECOND * durationSeconds, amplifier);
        }
        target.addPotionEffect(newEffect);
    }
}
