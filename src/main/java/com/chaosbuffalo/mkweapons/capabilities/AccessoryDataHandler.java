package com.chaosbuffalo.mkweapons.capabilities;

import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.accessories.MKAccessory;
import com.chaosbuffalo.mkweapons.items.effects.IItemEffect;
import com.chaosbuffalo.mkweapons.items.effects.ItemEffects;
import com.chaosbuffalo.mkweapons.items.effects.accesory.IAccessoryEffect;
import com.mojang.serialization.Dynamic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AccessoryDataHandler implements IAccessoryData, INBTSerializable<CompoundNBT> {

    protected final ItemStack stack;
    protected final List<IAccessoryEffect> dynamicEffects;

    public AccessoryDataHandler(ItemStack itemStack) {
        this.stack = itemStack;
        dynamicEffects = new ArrayList<>();
    }

    @Override
    public ItemStack getItemStack() {
        return stack;
    }

    public void addEffect(IAccessoryEffect effect) {
        dynamicEffects.add(effect);
        onDynamicEffectsChanged();
    }

    protected void onDynamicEffectsChanged() {
    }

    public void removeEffect(int index) {
        dynamicEffects.remove(index);
        onDynamicEffectsChanged();
    }

    @Override
    public void forAllStackEffects(Consumer<IAccessoryEffect> consumer) {
        if (stack.getItem() instanceof MKAccessory) {
            MKAccessory accessory = (MKAccessory) stack.getItem();
            accessory.forEachStaticAccessoryEffect(consumer);
        }
        dynamicEffects.forEach(consumer);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        ListNBT effectList = new ListNBT();
        for (IAccessoryEffect effect : dynamicEffects) {
            effectList.add(effect.serialize(NBTDynamicOps.INSTANCE));
        }
        nbt.put("accessory_effects", effectList);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("accessory_effects")) {
            ListNBT effectList = nbt.getList("accessory_effects", Constants.NBT.TAG_COMPOUND);
            dynamicEffects.clear();
            for (INBT effectNBT : effectList) {
                IItemEffect effect = ItemEffects.deserializeEffect(new Dynamic<>(NBTDynamicOps.INSTANCE, effectNBT));
                if (effect instanceof IAccessoryEffect) {
                    addEffect((IAccessoryEffect) effect);
                } else {
                    MKWeapons.LOGGER.error("Failed to deserialize accessory effect of type {} for item {}", effect.getTypeName(),
                            getItemStack());
                }
            }
        }
    }
}
