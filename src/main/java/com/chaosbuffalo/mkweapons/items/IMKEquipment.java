package com.chaosbuffalo.mkweapons.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public interface IMKEquipment {

    default void addToTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {

    }

    void onEquipped(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlotType slotType);

    void onUnequipped(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlotType slotType);
}
