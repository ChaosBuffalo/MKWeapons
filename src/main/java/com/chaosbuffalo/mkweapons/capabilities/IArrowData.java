package com.chaosbuffalo.mkweapons.capabilities;

import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface IArrowData extends INBTSerializable<CompoundNBT> {
    ItemStack getShootingWeapon();

    AbstractArrowEntity getArrow();

    void attach(AbstractArrowEntity arrow);

    void setShootingWeapon(ItemStack shootingWeapon);
}
