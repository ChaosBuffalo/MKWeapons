package com.chaosbuffalo.mkweapons.items.armor;

import com.chaosbuffalo.mkweapons.items.IMKEquipment;
import com.chaosbuffalo.mkweapons.items.effects.armor.IArmorEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public interface IMKArmor extends IMKEquipment {

    EquipmentSlotType getEquipmentSlot();

    @Override
    default void onEquipped(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlotType slotType) {
        forEachEffect(itemStack, armorEffect -> armorEffect.onEntityEquip(livingEntity));
    }

    @Override
    default void onUnequipped(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlotType slotType) {
        forEachEffect(itemStack, armorEffect -> armorEffect.onEntityUnequip(livingEntity));
    }

    List<? extends IArmorEffect> getArmorEffects(ItemStack item);

    List<? extends IArmorEffect> getArmorEffects();

    void forEachEffect(ItemStack stack, Consumer<IArmorEffect> consumer);
}
