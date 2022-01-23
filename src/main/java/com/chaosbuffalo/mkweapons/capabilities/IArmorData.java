package com.chaosbuffalo.mkweapons.capabilities;

import com.chaosbuffalo.mkweapons.items.effects.armor.IArmorEffect;
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

    void addArmorEffect(IArmorEffect armorEffect);

    void removeArmorEffect(int index);
}
