package com.chaosbuffalo.mkweapons.items.randomization.options;

import com.chaosbuffalo.mkweapons.items.randomization.slots.IRandomizationSlot;
import com.chaosbuffalo.mkweapons.items.randomization.slots.LootSlot;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Set;

public interface IRandomizationOption {

    ResourceLocation getName();

    void applyToItemStackForSlot(ItemStack stack, LootSlot slot);

    boolean isApplicableToItem(ItemStack stack);

    <D> D serialize(DynamicOps<D> ops);

    <D> void deserialize(Dynamic<D> dynamic);

    Set<LootSlot> getApplicableSlots();

    double getWeight();

    void setWeight(double weight);

    void setSlot(IRandomizationSlot slot);

    void addApplicableSlot(LootSlot slot);

    IRandomizationSlot getSlot();
}
