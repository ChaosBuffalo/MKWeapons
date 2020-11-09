package com.chaosbuffalo.mkweapons.items.weapon.effects.melee;

import com.chaosbuffalo.mkcore.GameConstants;
import com.chaosbuffalo.mkcore.effects.SpellCast;
import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BleedMeleeWeaponEffect implements IMeleeWeaponEffect {

    private final float damageMultiplier;
    private final int maxStacks;
    private final int durationSeconds;

    public BleedMeleeWeaponEffect(float damageMultiplier, int maxStacks, int durationSeconds){
        this.damageMultiplier = damageMultiplier;
        this.maxStacks = maxStacks;
        this.durationSeconds = durationSeconds;
    }

    @Override
    public void onHit(IMKMeleeWeapon weapon, ItemStack stack,
                      LivingEntity target, LivingEntity attacker) {
        EffectInstance effect = target.getActivePotionEffect(com.chaosbuffalo.mkweapons.effects.BleedEffect.INSTANCE);
        float damagePerSecond = damageMultiplier * weapon.getDamageForTier() / durationSeconds;
        SpellCast cast = com.chaosbuffalo.mkweapons.effects.BleedEffect.Create(attacker, damagePerSecond, damagePerSecond).setTarget(target);
        EffectInstance newEffect;
        if (effect == null){
           newEffect = cast.toPotionEffect(GameConstants.TICKS_PER_SECOND * durationSeconds + 10, 0);
        } else {
            int amplifier = Math.min(effect.getAmplifier() + 1, maxStacks);
            newEffect = cast.toPotionEffect(GameConstants.TICKS_PER_SECOND * durationSeconds + 10, amplifier);
        }
        target.addPotionEffect(newEffect);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("mkweapons.effect.bleed.name")
                .applyTextStyle(TextFormatting.DARK_RED));
        if (Screen.hasShiftDown()){
            tooltip.add(new StringTextComponent(I18n.format("mkweapons.effect.bleed.description",
                    damageMultiplier, durationSeconds, maxStacks)));
        }
    }
}
