package com.chaosbuffalo.mkweapons.capabilities;

import com.chaosbuffalo.mkcore.MKCoreRegistry;
import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.MKMeleeWeapon;
import com.chaosbuffalo.mkweapons.items.effects.IItemEffect;
import com.chaosbuffalo.mkweapons.items.effects.ItemModifierEffect;
import com.chaosbuffalo.mkweapons.items.weapon.IMKRangedWeapon;
import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import com.chaosbuffalo.mkweapons.items.effects.ItemEffects;
import com.chaosbuffalo.mkweapons.items.effects.melee.IMeleeWeaponEffect;
import com.chaosbuffalo.mkweapons.items.effects.ranged.IRangedWeaponEffect;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Dynamic;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeaponDataHandler implements IWeaponData {

    private ItemStack itemStack;
    private final List<IMeleeWeaponEffect> meleeWeaponEffects;
    private final List<IMeleeWeaponEffect> cachedMeleeWeaponEffects;
    private final List<IRangedWeaponEffect> rangedWeaponEffects;
    private final List<IRangedWeaponEffect> cachedRangedWeaponEffects;
    private final Map<EquipmentSlotType, Multimap<Attribute, AttributeModifier>> modifiers = new HashMap<>();
    private ResourceLocation ability;
    private boolean isCacheDirty;

    public WeaponDataHandler(){
        meleeWeaponEffects = new ArrayList<>();
        cachedMeleeWeaponEffects = new ArrayList<>();
        rangedWeaponEffects = new ArrayList<>();
        cachedRangedWeaponEffects = new ArrayList<>();
        isCacheDirty = true;
        ability = MKCoreRegistry.INVALID_ABILITY;
    }

    @Override
    public void attach(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public ItemStack getItemStack() {
        return itemStack;
    }

    private List<IMeleeWeaponEffect> getStackMeleeEffects() {
        return meleeWeaponEffects;
    }

    @Override
    public List<IMeleeWeaponEffect> getMeleeEffects() {
        if (isCacheDirty){
            cachedMeleeWeaponEffects.clear();
            if (getItemStack().getItem() instanceof IMKMeleeWeapon){
                cachedMeleeWeaponEffects.addAll(((IMKMeleeWeapon) getItemStack().getItem()).getWeaponEffects());
            }
            cachedMeleeWeaponEffects.addAll(getStackMeleeEffects());
            isCacheDirty = false;
        }
        return cachedMeleeWeaponEffects;
    }

    @Override
    public ResourceLocation getAbilityName() {
        return ability;
    }

    @Override
    public void setAbilityId(ResourceLocation ability) {
        this.ability = ability;
    }

    @Override
    public boolean hasMeleeWeaponEffects() {
        return !meleeWeaponEffects.isEmpty();
    }

    @Override
    public void markCacheDirty() {
        isCacheDirty = true;
        modifiers.clear();
    }

    @Override
    public void addMeleeWeaponEffect(IMeleeWeaponEffect weaponEffect) {
        meleeWeaponEffects.add(weaponEffect);
        markCacheDirty();
    }

    @Override
    public void removeMeleeWeaponEffect(int index) {
        meleeWeaponEffects.remove(index);
        markCacheDirty();
    }

    @Override
    public boolean hasRangedWeaponEffects() {
        return !rangedWeaponEffects.isEmpty();
    }

    @Override
    public void addRangedWeaponEffect(IRangedWeaponEffect weaponEffect) {
        rangedWeaponEffects.add(weaponEffect);
        markCacheDirty();
    }

    @Override
    public void removeRangedWeaponEffect(int index) {
        rangedWeaponEffects.remove(index);
        markCacheDirty();
    }

    private List<IRangedWeaponEffect> getStackRangedEffects() {
        return rangedWeaponEffects;
    }

    @Override
    public List<IRangedWeaponEffect> getRangedEffects() {
        if (isCacheDirty){
            cachedRangedWeaponEffects.clear();
            if (getItemStack().getItem() instanceof IMKRangedWeapon){
                cachedRangedWeaponEffects.addAll(((IMKRangedWeapon) getItemStack().getItem()).getWeaponEffects());
            }
            cachedRangedWeaponEffects.addAll(getStackRangedEffects());
            isCacheDirty = false;
        }
        return cachedRangedWeaponEffects;
    }

    private void loadSlotModifiers(EquipmentSlotType slot){
        Multimap<Attribute, AttributeModifier> modifiers = getItemStack().getItem().getAttributeModifiers(slot);
        Multimap<Attribute, AttributeModifier> newMods = HashMultimap.create();
        newMods.putAll(modifiers);
        if (slot == EquipmentSlotType.MAINHAND){
            if (itemStack.getItem() instanceof MKMeleeWeapon){
                for (IMeleeWeaponEffect weaponEffect : getMeleeEffects()) {
                    if (weaponEffect instanceof ItemModifierEffect) {
                        ItemModifierEffect modEffect = (ItemModifierEffect) weaponEffect;
                        modEffect.getModifiers().forEach(e -> newMods.put(e.getAttribute(), e.getModifier()));
                    }
                }
            } else {
                for (IRangedWeaponEffect weaponEffect : getRangedEffects()) {
                    if (weaponEffect instanceof ItemModifierEffect) {
                        ItemModifierEffect modEffect = (ItemModifierEffect) weaponEffect;
                        modEffect.getModifiers().forEach(e -> newMods.put(e.getAttribute(), e.getModifier()));
                    }
                }
            }
        }
        this.modifiers.put(slot, newMods);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot) {
        if (!modifiers.containsKey(slot)){
            loadSlotModifiers(slot);
        }
        return modifiers.get(slot);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        ListNBT effectList = new ListNBT();
        for (IMeleeWeaponEffect effect : getStackMeleeEffects()){
            effectList.add(effect.serialize(NBTDynamicOps.INSTANCE));
        }
        nbt.put("melee_effects", effectList);
        ListNBT rangedEffectList = new ListNBT();
        for (IRangedWeaponEffect effect : getStackRangedEffects()){
            rangedEffectList.add(effect.serialize(NBTDynamicOps.INSTANCE));
        }
        nbt.put("ranged_effects", rangedEffectList);
        ResourceLocation abilityId = getAbilityName() != null ? getAbilityName() : MKCoreRegistry.INVALID_ABILITY;
        nbt.putString("ability_granted", abilityId.toString());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("melee_effects")){
            ListNBT effectList = nbt.getList("melee_effects", Constants.NBT.TAG_COMPOUND);
            for (INBT effectNBT : effectList){
                IItemEffect effect = ItemEffects.deserializeEffect(new Dynamic<>(NBTDynamicOps.INSTANCE, effectNBT));
                if (effect instanceof IMeleeWeaponEffect){
                    addMeleeWeaponEffect((IMeleeWeaponEffect) effect);
                } else {
                    MKWeapons.LOGGER.error("Failed to deserialize melee effect of type {} for item {}", effect.getTypeName(),
                            getItemStack());
                }
            }
        }
        if (nbt.contains("ranged_effects")){
            ListNBT rangedEffectList = nbt.getList("ranged_effects", Constants.NBT.TAG_COMPOUND);
            for (INBT effectNBT : rangedEffectList){
                IItemEffect effect = ItemEffects.deserializeEffect(new Dynamic<>(NBTDynamicOps.INSTANCE, effectNBT));
                if (effect instanceof IRangedWeaponEffect){
                    addRangedWeaponEffect((IRangedWeaponEffect) effect);
                } else {
                    MKWeapons.LOGGER.error("Failed to deserialize ranged effect of type {} for item {}", effect.getTypeName(),
                            getItemStack());
                }
            }
        }
        ResourceLocation abilityId = new ResourceLocation(nbt.getString("ability_granted"));
        if (!abilityId.equals(MKCoreRegistry.INVALID_ABILITY)){
            setAbilityId(abilityId);
        }
    }

    public static class Storage implements Capability.IStorage<IWeaponData> {

        @Nullable
        @Override
        public INBT writeNBT(Capability<IWeaponData> capability, IWeaponData instance, Direction side) {
            if (instance == null){
                return null;
            }
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<IWeaponData> capability, IWeaponData instance, Direction side, INBT nbt) {
            if (nbt instanceof CompoundNBT && instance != null) {
                CompoundNBT tag = (CompoundNBT) nbt;
                instance.deserializeNBT(tag);
            }
        }
    }
}
