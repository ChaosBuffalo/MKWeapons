package com.chaosbuffalo.mkweapons.capabilities;

import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ArrowDataProvider implements ICapabilitySerializable<CompoundNBT> {

    private final ArrowDataHandler data;

    public ArrowDataProvider(AbstractArrowEntity arrow) {
        data = new ArrowDataHandler();
        data.attach(arrow);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return WeaponsCapabilities.ARROW_DATA_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> data));
    }

    @Override
    public CompoundNBT serializeNBT() {
        return (CompoundNBT) WeaponsCapabilities.ARROW_DATA_CAPABILITY.getStorage().writeNBT(
                WeaponsCapabilities.ARROW_DATA_CAPABILITY, data, null);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        WeaponsCapabilities.ARROW_DATA_CAPABILITY.getStorage().readNBT(
                WeaponsCapabilities.ARROW_DATA_CAPABILITY, data, null, nbt);
    }
}
