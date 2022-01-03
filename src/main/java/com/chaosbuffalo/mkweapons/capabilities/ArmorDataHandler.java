package com.chaosbuffalo.mkweapons.capabilities;

import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.armor.IMKArmor;
import com.chaosbuffalo.mkweapons.items.effects.IItemEffect;
import com.chaosbuffalo.mkweapons.items.effects.ItemEffects;
import com.chaosbuffalo.mkweapons.items.effects.armor.IArmorEffect;
import com.mojang.serialization.Dynamic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ArmorDataHandler implements IArmorData {

    private ItemStack itemStack;
    private final List<IArmorEffect> armorEffects;
    private final List<IArmorEffect> cachedArmorEffects;
    private boolean isCacheDirty;

    public ArmorDataHandler(){
        armorEffects = new ArrayList<>();
        cachedArmorEffects = new ArrayList<>();
        isCacheDirty = true;
    }

    @Override
    public void attach(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public List<IArmorEffect> getArmorEffects() {
        return armorEffects;
    }

    @Override
    public List<IArmorEffect> getCachedArmorEffects() {
        if (isCacheDirty){
            cachedArmorEffects.clear();
            if (getItemStack().getItem() instanceof IMKArmor){
                cachedArmorEffects.addAll(((IMKArmor) getItemStack().getItem()).getArmorEffects());
            }
            cachedArmorEffects.addAll(getArmorEffects());
            isCacheDirty = false;
        }
        return cachedArmorEffects;
    }

    @Override
    public boolean hasArmorEffects() {
        return !armorEffects.isEmpty();
    }


    @Override
    public void markCacheDirty() {
        isCacheDirty = true;
    }


    @Override
    public void addArmorEffect(IArmorEffect armorEffect) {
        armorEffects.add(armorEffect);
        markCacheDirty();
    }

    @Override
    public void removeArmorEffect(int index) {
        armorEffects.remove(index);
        markCacheDirty();
    }



    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        ListNBT effectList = new ListNBT();
        for (IArmorEffect effect : getArmorEffects()){
            effectList.add(effect.serialize(NBTDynamicOps.INSTANCE));
        }
        nbt.put("armor_effects", effectList);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("armor_effects")){
            ListNBT effectList = nbt.getList("armor_effects", Constants.NBT.TAG_COMPOUND);
            for (INBT effectNBT : effectList){
                IItemEffect effect = ItemEffects.deserializeEffect(new Dynamic<>(NBTDynamicOps.INSTANCE, effectNBT));
                if (effect instanceof IArmorEffect){
                    armorEffects.add((IArmorEffect) effect);
                } else {
                    MKWeapons.LOGGER.error("Failed to deserialize armor effect of type {} for item {}", effect.getType(),
                            getItemStack());
                }
            }
        }
    }

    public static class Storage implements Capability.IStorage<IArmorData> {

        @Nullable
        @Override
        public INBT writeNBT(Capability<IArmorData> capability, IArmorData instance, Direction side) {
            if (instance == null){
                return null;
            }
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<IArmorData> capability, IArmorData instance, Direction side, INBT nbt) {
            if (nbt instanceof CompoundNBT && instance != null) {
                CompoundNBT tag = (CompoundNBT) nbt;
                instance.deserializeNBT(tag);
            }
        }
    }
}