package com.chaosbuffalo.mkweapons.capabilities;

import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class ArrowDataHandler implements IArrowData {

    private final AbstractArrowEntity arrow;
    private ItemStack shootingWeapon;

    public ArrowDataHandler(AbstractArrowEntity arrow) {
        this.arrow = arrow;
        shootingWeapon = ItemStack.EMPTY;
    }

    @Override
    public ItemStack getShootingWeapon() {
        return shootingWeapon;
    }

    @Override
    public void setShootingWeapon(ItemStack shootingWeapon) {
        this.shootingWeapon = shootingWeapon;
    }

    @Override
    public AbstractArrowEntity getArrow() {
        return arrow;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        ItemStackHelper.saveAllItems(tag, NonNullList.from(shootingWeapon));
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(1, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(nbt, itemStacks);
        shootingWeapon = itemStacks.get(0);
    }

}
