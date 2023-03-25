package com.chaosbuffalo.mkweapons.capabilities;

import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class ArrowDataHandler implements IArrowData{

    private AbstractArrow arrow;
    private ItemStack shootingWeapon;

    public ArrowDataHandler(){
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
    public AbstractArrow getArrow() {
        return arrow;
    }

    @Override
    public void attach(AbstractArrow arrow) {
        this.arrow = arrow;
//        if (arrow.getShooter() instanceof LivingEntity){
//            ItemStack main = ((LivingEntity) arrow.getShooter()).getHeldItemMainhand();
//            shootingWeapon = main;
//        } else {
//            shootingWeapon = ItemStack.EMPTY;
//        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ContainerHelper.saveAllItems(tag, NonNullList.of(shootingWeapon));
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        NonNullList<ItemStack> itemStacks = NonNullList.withSize(1, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(nbt, itemStacks);
        shootingWeapon = itemStacks.get(0);
    }

    public static class Storage implements Capability.IStorage<IArrowData> {


        @Nullable
        @Override
        public Tag writeNBT(Capability<IArrowData> capability, IArrowData instance, Direction side) {
            if (instance == null){
                return null;
            }
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<IArrowData> capability, IArrowData instance, Direction side, Tag nbt) {
            if (nbt instanceof CompoundTag && instance != null) {
                CompoundTag tag = (CompoundTag) nbt;
                instance.deserializeNBT(tag);
            }
        }
    }
}
