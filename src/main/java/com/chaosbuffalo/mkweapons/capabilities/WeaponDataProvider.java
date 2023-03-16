package com.chaosbuffalo.mkweapons.capabilities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WeaponDataProvider implements ICapabilitySerializable<CompoundNBT> {

    private final WeaponDataHandler weaponDataHandler;

    public WeaponDataProvider(ItemStack item) {
        weaponDataHandler = new WeaponDataHandler();
        weaponDataHandler.attach(item);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return WeaponsCapabilities.WEAPON_DATA_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> weaponDataHandler));
    }

    @Override
    public CompoundNBT serializeNBT() {
        return (CompoundNBT) WeaponsCapabilities.WEAPON_DATA_CAPABILITY.getStorage().writeNBT(
                WeaponsCapabilities.WEAPON_DATA_CAPABILITY, weaponDataHandler, null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        WeaponsCapabilities.WEAPON_DATA_CAPABILITY.getStorage().readNBT(
                WeaponsCapabilities.WEAPON_DATA_CAPABILITY, weaponDataHandler, null, nbt);
    }
}
