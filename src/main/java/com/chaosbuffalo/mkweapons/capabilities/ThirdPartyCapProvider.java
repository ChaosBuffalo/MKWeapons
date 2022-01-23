package com.chaosbuffalo.mkweapons.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class ThirdPartyCapProvider<CapTarget, CapType> implements ICapabilitySerializable<CompoundNBT> {
    protected final CapType data;
    private final LazyOptional<CapType> capOpt;

    public ThirdPartyCapProvider(CapTarget attached) {
        this.data = this.makeData(attached);
        this.capOpt = LazyOptional.of(() -> this.data);
    }

    abstract CapType makeData(CapTarget var1);

    abstract Capability<CapType> getCapability();

    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return this.getCapability().orEmpty(cap, this.capOpt);
    }

    public void invalidate() {
        this.capOpt.invalidate();
    }
}
