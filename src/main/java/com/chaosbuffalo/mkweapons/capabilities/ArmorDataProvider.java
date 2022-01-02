package com.chaosbuffalo.mkweapons.capabilities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ArmorDataProvider implements ICapabilitySerializable<CompoundNBT> {

    private final ArmorDataHandler armorDataHandler;

    public ArmorDataProvider(ItemStack item){
        armorDataHandler = new ArmorDataHandler();
        armorDataHandler.attach(item);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return WeaponsCapabilities.ARMOR_DATA_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> armorDataHandler));
    }

    @Override
    public CompoundNBT serializeNBT() {
        return (CompoundNBT) WeaponsCapabilities.ARMOR_DATA_CAPABILITY.getStorage().writeNBT(
                WeaponsCapabilities.ARMOR_DATA_CAPABILITY, armorDataHandler, null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        WeaponsCapabilities.ARMOR_DATA_CAPABILITY.getStorage().readNBT(
                WeaponsCapabilities.ARMOR_DATA_CAPABILITY, armorDataHandler, null, nbt);
    }
}