package com.chaosbuffalo.mkweapons.capabilities;

import com.chaosbuffalo.mkweapons.items.effects.accesory.IAccessoryEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.function.Consumer;

public interface IAccessoryData extends INBTSerializable<CompoundNBT> {

    ItemStack getItemStack();

    void addEffect(IAccessoryEffect effect);

    void forAllStackEffects(Consumer<IAccessoryEffect> consumer);

}
