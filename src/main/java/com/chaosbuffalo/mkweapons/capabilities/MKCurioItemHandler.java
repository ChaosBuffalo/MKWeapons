package com.chaosbuffalo.mkweapons.capabilities;

import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.accessories.MKAccessory;
import com.chaosbuffalo.mkweapons.items.effects.IItemEffect;
import com.chaosbuffalo.mkweapons.items.effects.ItemEffects;
import com.chaosbuffalo.mkweapons.items.effects.accesory.IAccessoryEffect;
import com.mojang.serialization.Dynamic;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MKCurioItemHandler implements ICurio, INBTSerializable<CompoundNBT> {

    private final ItemStack stack;
    private final List<IAccessoryEffect> effects;
    private final List<IAccessoryEffect> cachedEffects;
    private boolean isCacheDirty;

    public MKCurioItemHandler(ItemStack itemStack){
        this.stack = itemStack;
        effects = new ArrayList<>();
        cachedEffects = new ArrayList<>();
        isCacheDirty = true;
    }

    public List<IAccessoryEffect> getEffects() {
        return effects;
    }

    public ItemStack getItemStack() {
        return stack;
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack) {
        for (IAccessoryEffect effect : getCachedEffects()){
            effect.onEntityEquip(slotContext.getWearer());
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack) {
        for (IAccessoryEffect effect : getCachedEffects()){
            effect.onEntityUnequip(slotContext.getWearer());
        }
    }

    public void addEffect(IAccessoryEffect effect) {
        effects.add(effect);
        markCacheDirty();
    }

    public void markCacheDirty() {
        isCacheDirty = true;
    }

    public void removeEffect(int index) {
        effects.remove(index);
        markCacheDirty();
    }

    public MKAccessory getAccessory(){
        return (MKAccessory) getItemStack().getItem();
    }

    public List<IAccessoryEffect> getCachedEffects() {
        if (isCacheDirty){
            cachedEffects.clear();
            if (getItemStack().getItem() instanceof MKAccessory){
                cachedEffects.addAll(((MKAccessory) getItemStack().getItem()).getAccessoryEffects());
            }
            cachedEffects.addAll(getEffects());
            isCacheDirty = false;
        }
        return cachedEffects;
    }

    public boolean hasEffects(){
        return !effects.isEmpty();
    }


    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        ListNBT effectList = new ListNBT();
        for (IAccessoryEffect effect : getEffects()){
            effectList.add(effect.serialize(NBTDynamicOps.INSTANCE));
        }
        nbt.put("accessory_effects", effectList);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("accessory_effects")){
            ListNBT effectList = nbt.getList("accessory_effects", Constants.NBT.TAG_COMPOUND);
            effects.clear();
            for (INBT effectNBT : effectList){
                IItemEffect effect = ItemEffects.deserializeEffect(new Dynamic<>(NBTDynamicOps.INSTANCE, effectNBT));
                if (effect instanceof IAccessoryEffect){
                    addEffect((IAccessoryEffect) effect);
                } else {
                    MKWeapons.LOGGER.error("Failed to deserialize accessory effect of type {} for item {}", effect.getTypeName(),
                            getItemStack());
                }
            }
        }
    }
}
