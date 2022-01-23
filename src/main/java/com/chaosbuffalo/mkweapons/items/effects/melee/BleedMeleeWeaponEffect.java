package com.chaosbuffalo.mkweapons.items.effects.melee;

import com.chaosbuffalo.mkcore.GameConstants;
import com.chaosbuffalo.mkcore.MKCore;
import com.chaosbuffalo.mkcore.abilities.MKAbility;
import com.chaosbuffalo.mkcore.core.MKAttributes;
import com.chaosbuffalo.mkcore.effects.MKEffectBuilder;
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

    public BleedMeleeWeaponEffect(float damageMultiplier, int maxStacks, int durationSeconds) {
        this();
        this.damageMultiplier = damageMultiplier;
        this.maxStacks = maxStacks;
        this.durationSeconds = durationSeconds;
    }

    public BleedMeleeWeaponEffect() {
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
    public <D> void readAdditionalData(Dynamic<D> dynamic) {
        super.readAdditionalData(dynamic);
        setMaxStacks(dynamic.get("maxStacks").asInt(5));
        setDurationSeconds(dynamic.get("durationSeconds").asInt(5));
        setDamageMultiplier(dynamic.get("damageMultiplier").asFloat(2.0f));
    }

    @Override
    public <D> void writeAdditionalData(DynamicOps<D> ops, ImmutableMap.Builder<D, D> builder) {
        super.writeAdditionalData(ops, builder);
        builder.put(ops.createString("damageMultiplier"), ops.createFloat(damageMultiplier));
        builder.put(ops.createString("maxStacks"), ops.createInt(maxStacks));
        builder.put(ops.createString("durationSeconds"), ops.createInt(durationSeconds));
    }

    @Override
    public void onHit(IMKMeleeWeapon weapon, ItemStack stack,
                      LivingEntity target, LivingEntity attacker) {
        float damagePerSecond = damageMultiplier * weapon.getDamageForTier() / durationSeconds;
        float skill = MKAbility.getSkillLevel(attacker, MKAttributes.PANKRATION);
        MKCore.getPlayer(attacker).ifPresent(x -> x.getSkills().tryIncreaseSkill(MKAttributes.PANKRATION));
        MKCore.getEntityData(target).ifPresent(targetData -> {
            MKEffectBuilder<?> effect = BleedEffect.from(attacker, maxStacks, damagePerSecond, damagePerSecond, 1f)
                    .periodic(GameConstants.TICKS_PER_SECOND) // tick every second
                    .timed(GameConstants.TICKS_PER_SECOND * durationSeconds + 10)
                    .skillLevel(skill);
            targetData.getEffects().addEffect(effect);
        });
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
        super.addInformation(stack, worldIn, tooltip);
        if (Screen.hasShiftDown()) {
            tooltip.add(new StringTextComponent(I18n.format("mkweapons.weapon_effect.bleed.description",
                    damageMultiplier, durationSeconds, maxStacks)));
        }
    }

}
