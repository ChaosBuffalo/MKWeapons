package com.chaosbuffalo.mkweapons.capabilities;

import com.chaosbuffalo.mkweapons.MKWeapons;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WeaponsCapabilities {

    public static ResourceLocation MK_ARROW_CAP_ID = new ResourceLocation(MKWeapons.MODID, "arrow_data");
    public static ResourceLocation MK_WEAPON_CAP_ID = new ResourceLocation(MKWeapons.MODID, "melee_weapon_data");
    public static ResourceLocation MK_ARMOR_CAP_ID = new ResourceLocation(MKWeapons.MODID, "armor_data");

    @CapabilityInject(IArrowData.class)
    public static final Capability<IArrowData> ARROW_DATA_CAPABILITY;

    @CapabilityInject(IWeaponData.class)
    public static final Capability<IWeaponData> WEAPON_DATA_CAPABILITY;

    @CapabilityInject(IMeleeWeaponData.class)
    public static final Capability<IMeleeWeaponData> MELEE_WEAPON_DATA_CAPABILITY;

    @CapabilityInject(IRangedWeaponData.class)
    public static final Capability<IRangedWeaponData> RANGED_WEAPON_DATA_CAPABILITY;

    @CapabilityInject(IArmorData.class)
    public static final Capability<IArmorData> ARMOR_DATA_CAPABILITY;

    static {
        ARROW_DATA_CAPABILITY = shutUpAboutThisBeingNull();
        WEAPON_DATA_CAPABILITY = shutUpAboutThisBeingNull();
        MELEE_WEAPON_DATA_CAPABILITY = shutUpAboutThisBeingNull();
        RANGED_WEAPON_DATA_CAPABILITY = shutUpAboutThisBeingNull();
        ARMOR_DATA_CAPABILITY = shutUpAboutThisBeingNull();
    }

    @SuppressWarnings("DataFlowIssue")
    @Nonnull
    private static <T> T shutUpAboutThisBeingNull() {
        return null;
    }

    public static void registerCapabilities() {
        CapabilityManager.INSTANCE.register(IArrowData.class, new SimpleStorage<>(), () -> null);
        CapabilityManager.INSTANCE.register(IWeaponData.class, new SimpleStorage<>(), () -> null);
        CapabilityManager.INSTANCE.register(IMeleeWeaponData.class, new SimpleStorage<>(), () -> null);
        CapabilityManager.INSTANCE.register(IRangedWeaponData.class, new SimpleStorage<>(), () -> null);
        CapabilityManager.INSTANCE.register(IArmorData.class, new SimpleStorage<>(), () -> null);
    }

    private static class SimpleStorage<T extends INBTSerializable<CompoundNBT>> implements Capability.IStorage<T> {

        @Nullable
        @Override
        public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {
            if (nbt instanceof CompoundNBT) {
                instance.deserializeNBT((CompoundNBT) nbt);
            }
        }
    }
}
