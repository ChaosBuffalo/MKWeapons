package com.chaosbuffalo.mkweapons.capabilities;

import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.accessories.MKAccessory;
import com.chaosbuffalo.mkweapons.items.effects.IItemEffect;
import com.chaosbuffalo.mkweapons.items.effects.ItemEffects;
import com.chaosbuffalo.mkweapons.items.effects.ItemModifierEffect;
import com.chaosbuffalo.mkweapons.items.effects.accesory.IAccessoryEffect;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Dynamic;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.*;

public class MKCurioItemHandler implements ICurio, INBTSerializable<CompoundNBT> {

    private final ItemStack stack;
    private final List<IAccessoryEffect> effects;
    private final List<IAccessoryEffect> cachedEffects;
    private final Map<String, Multimap<Attribute, AttributeModifier>> modifiers = new HashMap<>();
    private boolean isCacheDirty;

    public MKCurioItemHandler(ItemStack itemStack) {
        this.stack = itemStack;
        effects = new ArrayList<>();
        cachedEffects = new ArrayList<>();
        isCacheDirty = true;
    }

    private List<IAccessoryEffect> getStackEffects() {
        return effects;
    }

    public ItemStack getItemStack() {
        return stack;
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack) {
        for (IAccessoryEffect effect : getEffects()) {
            effect.onEntityEquip(slotContext.getWearer());
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack) {
        for (IAccessoryEffect effect : getEffects()) {
            effect.onEntityUnequip(slotContext.getWearer());
        }
    }

    public void addEffect(IAccessoryEffect effect) {
        effects.add(effect);
        markCacheDirty();
    }

    public void markCacheDirty() {
        isCacheDirty = true;
        modifiers.clear();
    }

    public void removeEffect(int index) {
        effects.remove(index);
        markCacheDirty();
    }

    public MKAccessory getAccessory() {
        return (MKAccessory) getItemStack().getItem();
    }

    public List<IAccessoryEffect> getEffects() {
        if (isCacheDirty) {
            cachedEffects.clear();
            if (getItemStack().getItem() instanceof MKAccessory) {
                cachedEffects.addAll(((MKAccessory) getItemStack().getItem()).getAccessoryEffects());
            }
            cachedEffects.addAll(getStackEffects());
            isCacheDirty = false;
        }
        return cachedEffects;
    }

    public boolean hasEffects() {
        return !effects.isEmpty();
    }

    private void loadSlotModifiers(String slotId) {
        Multimap<Attribute, AttributeModifier> newMods = HashMultimap.create();
        for (IAccessoryEffect weaponEffect : getEffects()) {
            if (weaponEffect instanceof ItemModifierEffect) {
                ItemModifierEffect modEffect = (ItemModifierEffect) weaponEffect;
                modEffect.getModifiers().forEach(e -> newMods.put(e.getAttribute(), e.getModifier()));
            }
        }
        this.modifiers.put(slotId, newMods);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid) {
        if (!modifiers.containsKey(slotContext.getIdentifier())) {
            loadSlotModifiers(slotContext.getIdentifier());
        }
        return modifiers.get(slotContext.getIdentifier());
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        ListNBT effectList = new ListNBT();
        for (IAccessoryEffect effect : getStackEffects()) {
            effectList.add(effect.serialize(NBTDynamicOps.INSTANCE));
        }
        nbt.put("accessory_effects", effectList);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("accessory_effects")) {
            ListNBT effectList = nbt.getList("accessory_effects", Constants.NBT.TAG_COMPOUND);
            effects.clear();
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
