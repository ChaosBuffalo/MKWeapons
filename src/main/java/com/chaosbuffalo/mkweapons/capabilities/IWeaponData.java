package com.chaosbuffalo.mkweapons.capabilities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public interface IWeaponData extends INBTSerializable<CompoundNBT> {

    ItemStack getItemStack();

    ResourceLocation getAbilityName();

    void setAbilityId(ResourceLocation ability);

}
