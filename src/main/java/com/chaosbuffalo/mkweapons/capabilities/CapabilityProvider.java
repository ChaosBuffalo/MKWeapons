package com.chaosbuffalo.mkweapons.capabilities;

import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import it.unimi.dsi.fastutil.objects.ReferenceSets;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Set;

public class CapabilityProvider<C extends INBTSerializable<CompoundNBT>>
        implements ICapabilityProvider, INBTSerializable<CompoundNBT> {

    protected final LazyOptional<C> capInstance;
    protected final Set<Capability<? super C>> supportedCapabilities;

    public CapabilityProvider(C instance, Set<Capability<? super C>> supportedCapabilities) {
        Objects.requireNonNull(instance);
        capInstance = LazyOptional.of(() -> instance);
        this.supportedCapabilities = supportedCapabilities;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (supportedCapabilities.contains(cap)) {
            return capInstance.cast();
        }
        return LazyOptional.empty();
    }

    public void addListener(NonNullConsumer<LazyOptional<C>> callback) {
        capInstance.addListener(callback);
    }

    @Override
    public CompoundNBT serializeNBT() {
        return capInstance.map(INBTSerializable::serializeNBT).orElseThrow(IllegalStateException::new);
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        capInstance.ifPresent(c -> c.deserializeNBT(nbt));
    }

    public static <C extends INBTSerializable<CompoundNBT>> ICapabilityProvider of(C instance,
                                                                                   Capability<? super C> capability) {
        return new CapabilityProvider<>(instance, ReferenceSets.singleton(capability));
    }

    public static <C extends INBTSerializable<CompoundNBT>> ICapabilityProvider of(C instance,
                                                                                   Capability<? super C> capability1,
                                                                                   Capability<? super C> capability2) {
        ReferenceSet<Capability<? super C>> set = new ReferenceArraySet<>(2);
        set.add(capability1);
        set.add(capability2);
        return new CapabilityProvider<>(instance, set);
    }
}
