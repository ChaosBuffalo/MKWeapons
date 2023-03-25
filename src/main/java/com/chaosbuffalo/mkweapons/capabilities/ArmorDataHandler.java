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
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.core.Direction;
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
    private final Map<EquipmentSlot, Multimap<Attribute, AttributeModifier>> modifiers = new HashMap<>();
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

    private void loadSlotModifiers(EquipmentSlot slot){
        Multimap<Attribute, AttributeModifier> modifiers = getItemStack().getItem().getDefaultAttributeModifiers(slot);
        Multimap<Attribute, AttributeModifier> newMods = HashMultimap.create();
        newMods.putAll(modifiers);
        if (slot == getArmorItem().getSlot()){
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
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
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
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        ListTag effectList = new ListTag();
        for (IArmorEffect effect : getStackArmorEffects()){
            effectList.add(effect.serialize(NbtOps.INSTANCE));
        }
        nbt.put("armor_effects", effectList);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("armor_effects")){
            ListTag effectList = nbt.getList("armor_effects", Constants.NBT.TAG_COMPOUND);
            for (Tag effectNBT : effectList){
                IItemEffect effect = ItemEffects.deserializeEffect(new Dynamic<>(NbtOps.INSTANCE, effectNBT));
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
        public Tag writeNBT(Capability<IArmorData> capability, IArmorData instance, Direction side) {
            if (instance == null){
                return null;
            }
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<IArmorData> capability, IArmorData instance, Direction side, Tag nbt) {
            if (nbt instanceof CompoundTag && instance != null) {
                CompoundTag tag = (CompoundTag) nbt;
                instance.deserializeNBT(tag);
            }
        }
    }
}