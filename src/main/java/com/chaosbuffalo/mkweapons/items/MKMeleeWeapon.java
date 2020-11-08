package com.chaosbuffalo.mkweapons.items;

import com.chaosbuffalo.mkcore.core.MKAttributes;
import com.chaosbuffalo.mkweapons.items.weapon.types.IMeleeWeaponType;
import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import com.chaosbuffalo.mkweapons.items.weapon.effects.IWeaponEffect;
import com.chaosbuffalo.mkweapons.items.weapon.tier.MKTier;
import com.google.common.collect.Multimap;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MKMeleeWeapon extends SwordItem implements IMKMeleeWeapon {
    private final IMeleeWeaponType weaponType;
    private final MKTier mkTier;
    private final List<IWeaponEffect> weaponEffects;
    protected static final UUID ATTACK_REACH_MODIFIER = UUID.fromString("f74aa80c-43b8-4d00-a6ce-8d52694ff20c");

    public MKMeleeWeapon(ResourceLocation weaponName, MKTier tier, IMeleeWeaponType weaponType, Properties builder) {
        super(tier, Math.round(weaponType.getDamageForTier(tier)), weaponType.getAttackSpeed(), builder);
        this.weaponType = weaponType;
        this.mkTier = tier;
        this.weaponEffects = new ArrayList<>();
        weaponEffects.addAll(tier.getWeaponEffects());
        weaponEffects.addAll(weaponType.getWeaponEffects());
        setRegistryName(weaponName);
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
        Multimap<String, AttributeModifier> map = super.getAttributeModifiers(equipmentSlot);
        if (equipmentSlot == EquipmentSlotType.MAINHAND) {
            map.put(MKAttributes.ATTACK_REACH.getName(), new AttributeModifier(ATTACK_REACH_MODIFIER, "Weapon modifier",
                    this.weaponType.getReach(), AttributeModifier.Operation.ADDITION));
        }
        return map;
    }

    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        for (IWeaponEffect effect : getWeaponEffects()){
            effect.onHit(this, stack, target, attacker);
        }
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
                getWeaponType().getCritChance() * 100.0f)));
        tooltip.add(new StringTextComponent(I18n.format("mkweapons.crit_multiplier.description",
                getWeaponType().getCritMultiplier())));
        if (getWeaponType().isTwoHanded()){
            tooltip.add(new TranslationTextComponent("mkweapons.two_handed.name")
                    .applyTextStyle(TextFormatting.DARK_GRAY));
            if (Screen.hasShiftDown()){
                tooltip.add(new TranslationTextComponent("mkweapons.two_handed.description"));
            }
        }
        for (IWeaponEffect effect : weaponEffects){
            effect.addInformation(stack, worldIn, tooltip, flagIn);
        }
    }

    @Override
    public List<IWeaponEffect> getWeaponEffects() {
        return weaponEffects;
    }
}
