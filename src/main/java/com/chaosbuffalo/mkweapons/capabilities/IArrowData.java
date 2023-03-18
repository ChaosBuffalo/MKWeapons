package com.chaosbuffalo.mkweapons.capabilities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public interface IArrowData extends INBTSerializable<CompoundNBT> {
    ItemStack getShootingWeapon();

    AbstractArrowEntity getArrow();

    void setShootingWeapon(ItemStack shootingWeapon);

    static LazyOptional<IArrowData> get(Entity entity) {
        return entity.getCapability(WeaponsCapabilities.ARROW_DATA_CAPABILITY);
    }
}
