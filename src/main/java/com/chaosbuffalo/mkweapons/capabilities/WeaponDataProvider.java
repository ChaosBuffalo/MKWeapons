package com.chaosbuffalo.mkweapons.capabilities;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WeaponDataProvider implements ICapabilitySerializable<CompoundTag> {

    private final WeaponDataHandler weaponDataHandler;

    public WeaponDataProvider(ItemStack item){
        weaponDataHandler = new WeaponDataHandler();
        weaponDataHandler.attach(item);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return WeaponsCapabilities.WEAPON_DATA_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> weaponDataHandler));
    }

    @Override
    public CompoundTag serializeNBT() {
        return (CompoundTag) WeaponsCapabilities.WEAPON_DATA_CAPABILITY.getStorage().writeNBT(
                WeaponsCapabilities.WEAPON_DATA_CAPABILITY, weaponDataHandler, null);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        WeaponsCapabilities.WEAPON_DATA_CAPABILITY.getStorage().readNBT(
                WeaponsCapabilities.WEAPON_DATA_CAPABILITY, weaponDataHandler, null, nbt);
    }
}
