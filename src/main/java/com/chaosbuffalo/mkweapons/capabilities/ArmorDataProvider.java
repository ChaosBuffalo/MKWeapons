package com.chaosbuffalo.mkweapons.capabilities;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ArmorDataProvider implements ICapabilitySerializable<CompoundTag> {

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
    public CompoundTag serializeNBT() {
        return (CompoundTag) WeaponsCapabilities.ARMOR_DATA_CAPABILITY.getStorage().writeNBT(
                WeaponsCapabilities.ARMOR_DATA_CAPABILITY, armorDataHandler, null);
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        WeaponsCapabilities.ARMOR_DATA_CAPABILITY.getStorage().readNBT(
                WeaponsCapabilities.ARMOR_DATA_CAPABILITY, armorDataHandler, null, nbt);
    }
}