package com.chaosbuffalo.mkweapons.capabilities;

import com.chaosbuffalo.mkweapons.items.effects.armor.IArmorEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.function.Consumer;

public interface IArmorData extends INBTSerializable<CompoundNBT> {

    ItemStack getItemStack();

    void forEachEffect(Consumer<IArmorEffect> consumer);

    void addArmorEffect(IArmorEffect armorEffect);

    void removeArmorEffect(int index);
}
