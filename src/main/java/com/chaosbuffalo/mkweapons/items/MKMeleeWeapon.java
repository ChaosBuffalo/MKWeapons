package com.chaosbuffalo.mkweapons.items;

import com.chaosbuffalo.mkcore.core.MKAttributes;
import com.chaosbuffalo.mkweapons.items.weapon.types.IMeleeWeaponType;
import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import com.chaosbuffalo.mkweapons.items.weapon.effects.IWeaponEffect;
import com.chaosbuffalo.mkweapons.items.weapon.tier.MKTier;
import com.google.common.collect.Multimap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.UUID;

public class MKMeleeWeapon extends SwordItem implements IMKMeleeWeapon {
    private final IMeleeWeaponType weaponType;
    private final MKTier mkTier;
    protected static final UUID ATTACK_REACH_MODIFIER = UUID.fromString("f74aa80c-43b8-4d00-a6ce-8d52694ff20c");

    public MKMeleeWeapon(ResourceLocation weaponName, MKTier tier, IMeleeWeaponType weaponType, Properties builder) {
        super(tier, Math.round(weaponType.getDamageForTier(tier)), weaponType.getAttackSpeed(), builder);
        this.weaponType = weaponType;
        this.mkTier = tier;
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
        for (IWeaponEffect effect : getWeaponType().getEffects()){
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
    public List<IWeaponEffect> getWeaponEffects() {
        return getWeaponType().getEffects();
    }
}
