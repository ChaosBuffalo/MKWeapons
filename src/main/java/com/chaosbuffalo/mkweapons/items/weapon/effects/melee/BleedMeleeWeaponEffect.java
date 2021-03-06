package com.chaosbuffalo.mkweapons.items.weapon.effects.melee;

import com.chaosbuffalo.mkcore.GameConstants;
import com.chaosbuffalo.mkcore.effects.SpellCast;
import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.effects.BleedEffect;
import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BleedMeleeWeaponEffect extends BaseMeleeWeaponEffect {

    private float damageMultiplier;
    private int maxStacks;
    private int durationSeconds;
    public static final ResourceLocation NAME = new ResourceLocation(MKWeapons.MODID, "weapon_effect.bleed");

    public BleedMeleeWeaponEffect(float damageMultiplier, int maxStacks, int durationSeconds){
        this();
        this.damageMultiplier = damageMultiplier;
        this.maxStacks = maxStacks;
        this.durationSeconds = durationSeconds;
    }

    public BleedMeleeWeaponEffect(){
        super(NAME, TextFormatting.DARK_RED);
    }

    public void setDamageMultiplier(float damageMultiplier) {
        this.damageMultiplier = damageMultiplier;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public void setMaxStacks(int maxStacks) {
        this.maxStacks = maxStacks;
    }

    @Override
    public <D> void deserialize(Dynamic<D> dynamic) {
        super.deserialize(dynamic);
        setMaxStacks(dynamic.get("maxStacks").asInt(5));
        setDurationSeconds(dynamic.get("durationSeconds").asInt(5));
        setDamageMultiplier(dynamic.get("damageMultiplier").asFloat(2.0f));
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops) {
        return ops.mergeToMap(super.serialize(ops), ImmutableMap.of(
                ops.createString("damageMultiplier"), ops.createFloat(damageMultiplier),
                ops.createString("maxStacks"), ops.createInt(maxStacks),
                ops.createString("durationSeconds"), ops.createInt(durationSeconds)
        )).result().orElse(ops.createMap(ImmutableMap.of()));
    }

    @Override
    public void onHit(IMKMeleeWeapon weapon, ItemStack stack,
                      LivingEntity target, LivingEntity attacker) {
        EffectInstance effect = target.getActivePotionEffect(BleedEffect.INSTANCE);
        float damagePerSecond = damageMultiplier * weapon.getDamageForTier() / durationSeconds;
        SpellCast cast = BleedEffect.Create(attacker, damagePerSecond, damagePerSecond).setTarget(target);
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
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
        super.addInformation(stack, worldIn, tooltip);
        if (Screen.hasShiftDown()){
            tooltip.add(new StringTextComponent(I18n.format("mkweapons.weapon_effect.bleed.description",
                    damageMultiplier, durationSeconds, maxStacks)));
        }
    }

}
