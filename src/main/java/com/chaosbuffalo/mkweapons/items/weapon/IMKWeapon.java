package com.chaosbuffalo.mkweapons.items.weapon;

import com.chaosbuffalo.mkcore.core.IMKAbilityProvider;
import com.chaosbuffalo.mkweapons.items.IMKEquipment;
import com.chaosbuffalo.mkweapons.items.effects.IItemEffect;
import com.chaosbuffalo.mkweapons.items.weapon.tier.MKTier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface IMKWeapon extends IMKEquipment, IMKAbilityProvider {

    MKTier getMKTier();

    default void reload() {

    }

    @Override
    default void onEquipped(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlotType slotType) {
        if (slotType == EquipmentSlotType.MAINHAND) {
            getWeaponEffects(itemStack).forEach(eff -> eff.onEntityEquip(livingEntity));
        }
    }

    @Override
    default void onUnequipped(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlotType slotType) {
        if (slotType == EquipmentSlotType.MAINHAND) {
            getWeaponEffects(itemStack).forEach(eff -> eff.onEntityUnequip(livingEntity));
        }
    }

    List<? extends IItemEffect> getWeaponEffects(ItemStack item);

}
