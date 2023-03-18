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
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface IArmorData extends INBTSerializable<CompoundNBT> {

    ItemStack getItemStack();

    void forEachEffect(Consumer<IArmorEffect> consumer);

    void addArmorEffect(IArmorEffect armorEffect);

    void removeArmorEffect(int index);
}
