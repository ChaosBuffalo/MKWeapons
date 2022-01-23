package com.chaosbuffalo.mkweapons.capabilities;

import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.armor.IMKArmor;
import com.chaosbuffalo.mkweapons.items.armor.MKArmorItem;
import com.chaosbuffalo.mkweapons.items.effects.IItemEffect;
import com.chaosbuffalo.mkweapons.items.effects.ItemEffects;
import com.chaosbuffalo.mkweapons.items.effects.ItemModifierEffect;
import com.chaosbuffalo.mkweapons.items.effects.armor.IArmorEffect;
import com.chaosbuffalo.mkweapons.items.effects.melee.IMeleeWeaponEffect;
import com.chaosbuffalo.mkweapons.items.effects.ranged.IRangedWeaponEffect;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Dynamic;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArmorDataHandler implements IArmorData {

    private ItemStack itemStack;
    private final List<IArmorEffect> armorEffects;
    private final List<IArmorEffect> cachedArmorEffects;
    private final Map<EquipmentSlotType, Multimap<Attribute, AttributeModifier>> modifiers = new HashMap<>();
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

    private List<IArmorEffect> getStackArmorEffects() {
        return armorEffects;
    }

    @Override
    public List<IArmorEffect> getArmorEffects() {
        if (isCacheDirty){
            cachedArmorEffects.clear();
            if (getItemStack().getItem() instanceof IMKArmor){
                cachedArmorEffects.addAll(((IMKArmor) getItemStack().getItem()).getArmorEffects());
            }
            cachedArmorEffects.addAll(getStackArmorEffects());
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

    private void loadSlotModifiers(EquipmentSlotType slot){
        Multimap<Attribute, AttributeModifier> modifiers = getItemStack().getItem().getAttributeModifiers(slot);
        Multimap<Attribute, AttributeModifier> newMods = HashMultimap.create();
        newMods.putAll(modifiers);
        if (slot == getArmorItem().getEquipmentSlot()){
            for (IArmorEffect armorEffect : getArmorEffects()) {
                if (armorEffect instanceof ItemModifierEffect) {
                    ItemModifierEffect modEffect = (ItemModifierEffect) armorEffect;
                    modEffect.getModifiers().forEach(e -> newMods.put(e.getAttribute(), e.getModifier()));
                }
            }
        }
        this.modifiers.put(slot, newMods);
    }

    private MKArmorItem getArmorItem(){
        return (MKArmorItem) itemStack.getItem();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot) {
        if (!modifiers.containsKey(slot)){
            loadSlotModifiers(slot);
        }
        return modifiers.get(slot);
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
        for (IArmorEffect effect : getStackArmorEffects()){
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
                    addArmorEffect((IArmorEffect) effect);
                } else {
                    MKWeapons.LOGGER.error("Failed to deserialize armor effect of type {} for item {}", effect.getTypeName(),
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