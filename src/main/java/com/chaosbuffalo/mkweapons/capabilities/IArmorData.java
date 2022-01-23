package com.chaosbuffalo.mkweapons.capabilities;

import com.chaosbuffalo.mkweapons.items.effects.armor.IArmorEffect;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;

public interface IArmorData extends INBTSerializable<CompoundNBT> {

    void attach(ItemStack itemStack);

    ItemStack getItemStack();

    List<IArmorEffect> getArmorEffects();

    boolean hasArmorEffects();

    void markCacheDirty();

    Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot);

    void addArmorEffect(IArmorEffect armorEffect);

    void removeArmorEffect(int index);
}
