package com.chaosbuffalo.mkweapons.items;

import com.chaosbuffalo.mkcore.MKCore;
import com.chaosbuffalo.mkcore.MKCoreRegistry;
import com.chaosbuffalo.mkcore.abilities.MKAbility;
import com.chaosbuffalo.mkcore.core.MKAttributes;
import com.chaosbuffalo.mkcore.utils.EntityUtils;
import com.chaosbuffalo.mkweapons.capabilities.IWeaponData;
import com.chaosbuffalo.mkweapons.capabilities.WeaponsCapabilities;
import com.chaosbuffalo.mkweapons.items.weapon.types.IMeleeWeaponType;
import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import com.chaosbuffalo.mkweapons.items.weapon.effects.melee.IMeleeWeaponEffect;
import com.chaosbuffalo.mkweapons.items.weapon.tier.MKTier;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;

public class MKMeleeWeapon extends SwordItem implements IMKMeleeWeapon {
    private final IMeleeWeaponType weaponType;
    private final MKTier mkTier;
    private final List<IMeleeWeaponEffect> weaponEffects;
    protected Multimap<Attribute, AttributeModifier> modifiers;
    protected static final UUID ATTACK_REACH_MODIFIER = UUID.fromString("f74aa80c-43b8-4d00-a6ce-8d52694ff20c");
    protected static final UUID CRIT_CHANCE_MODIFIER = UUID.fromString("9b9c4389-0036-4beb-9dcc-5e11928ff499");
    protected static final UUID CRIT_MULT_MODIFIER = UUID.fromString("11fc07d2-7844-44f2-94ad-02479cff424d");

    public MKMeleeWeapon(ResourceLocation weaponName, MKTier tier, IMeleeWeaponType weaponType, Properties builder) {
        super(tier, Math.round(weaponType.getDamageForTier(tier) - tier.getAttackDamage()), weaponType.getAttackSpeed(), builder);
        this.weaponType = weaponType;
        this.mkTier = tier;
        this.weaponEffects = new ArrayList<>();
        recalculateModifiers();
        weaponEffects.addAll(tier.getMeleeWeaponEffects());
        weaponEffects.addAll(weaponType.getWeaponEffects());
        setRegistryName(weaponName);
    }

    protected void recalculateModifiers(){
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ATTACK_DAMAGE_MODIFIER,
                "Weapon modifier", getAttackDamage(), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(ATTACK_SPEED_MODIFIER,
                "Weapon modifier", getWeaponType().getAttackSpeed(), AttributeModifier.Operation.ADDITION));
        builder.put(MKAttributes.ATTACK_REACH, new AttributeModifier(ATTACK_REACH_MODIFIER,
                "Weapon modifier", getWeaponType().getReach(), AttributeModifier.Operation.ADDITION));
        builder.put(MKAttributes.MELEE_CRIT, new AttributeModifier(CRIT_CHANCE_MODIFIER,
                "Weapon modifier", getWeaponType().getCritChance(), AttributeModifier.Operation.ADDITION));
        builder.put(MKAttributes.MELEE_CRIT_MULTIPLIER, new AttributeModifier(CRIT_MULT_MODIFIER,
                "Weapon modifier", getWeaponType().getCritMultiplier(), AttributeModifier.Operation.ADDITION));
        modifiers = builder.build();
    }

    @Override
    public float getAttackDamage() {
        return Math.round(getWeaponType().getDamageForTier(getMKTier()) - getMKTier().getAttackDamage());
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
        return equipmentSlot == EquipmentSlotType.MAINHAND ? this.modifiers : super.getAttributeModifiers(equipmentSlot);
    }

    @Override
    public void reload(){
        weaponEffects.clear();
        weaponEffects.addAll(getMKTier().getMeleeWeaponEffects());
        weaponEffects.addAll(getWeaponType().getWeaponEffects());
        recalculateModifiers();
    }

    @Override
    public List<IMeleeWeaponEffect> getWeaponEffects() {
        return weaponEffects;
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        MKCore.getEntityData(attacker).ifPresent(cap -> {
            if (cap.getCombatExtension().getEntityTicksSinceLastSwing() >= EntityUtils.getCooldownPeriod(attacker)){
                for (IMeleeWeaponEffect effect : getWeaponEffects(stack)){
                    effect.onHit(this, stack, target, attacker);
                }
            }
        });

        return super.hitEntity(stack, target, attacker);
    }

    @Override
    public IMeleeWeaponType getWeaponType() {
        return weaponType;
    }

    @Override
    public MKTier getMKTier() {
        return mkTier;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new StringTextComponent(I18n.format("mkweapons.crit_chance.description",
                getWeaponType().getCritChance() * 100.0f)).mergeStyle(TextFormatting.GRAY));
        tooltip.add(new StringTextComponent(I18n.format("mkweapons.crit_multiplier.description",
                getWeaponType().getCritMultiplier())).mergeStyle(TextFormatting.GRAY));
        if (getWeaponType().isTwoHanded()){
            tooltip.add(new TranslationTextComponent("mkweapons.two_handed.name")
                    .mergeStyle(TextFormatting.GRAY));
            if (Screen.hasShiftDown()){
                tooltip.add(new TranslationTextComponent("mkweapons.two_handed.description"));
            }
        }
        for (IMeleeWeaponEffect effect : getWeaponEffects(stack)){
            effect.addInformation(stack, worldIn, tooltip, flagIn);
        }
        MKAbility ability = getAbility(stack);
        if (ability != null){
            tooltip.add(new StringTextComponent(I18n.format("mkweapons.grants_ability",
                    ability.getAbilityName())).mergeStyle(TextFormatting.GOLD));
        }
    }

    @Override
    public List<IMeleeWeaponEffect> getWeaponEffects(ItemStack item) {
        return item.getCapability(WeaponsCapabilities.WEAPON_DATA_CAPABILITY).map(cap -> {
            if (cap.hasMeleeWeaponEffects()){
                return cap.getCachedMeleeWeaponEffects();
            } else {
                return weaponEffects;
            }
        }).orElse(weaponEffects);
    }

    @Nullable
    @Override
    public MKAbility getAbility(ItemStack itemStack) {
        return MKCoreRegistry.getAbility(itemStack.getCapability(WeaponsCapabilities.WEAPON_DATA_CAPABILITY)
                .map(IWeaponData::getAbilityName).orElse(MKCoreRegistry.INVALID_ABILITY));
    }
}
