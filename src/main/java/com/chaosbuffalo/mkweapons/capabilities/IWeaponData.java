package com.chaosbuffalo.mkweapons.capabilities;

import com.chaosbuffalo.mkweapons.items.effects.melee.IMeleeWeaponEffect;
import com.chaosbuffalo.mkweapons.items.effects.ranged.IRangedWeaponEffect;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;

public interface IWeaponData extends INBTSerializable<CompoundNBT> {

    void attach(ItemStack itemStack);

    ItemStack getItemStack();

    List<IMeleeWeaponEffect> getMeleeEffects();

    ResourceLocation getAbilityName();

    void setAbilityId(ResourceLocation ability);

    boolean hasMeleeWeaponEffects();

    void markCacheDirty();

    void addMeleeWeaponEffect(IMeleeWeaponEffect weaponEffect);

    void removeMeleeWeaponEffect(int index);

    boolean hasRangedWeaponEffects();

    void addRangedWeaponEffect(IRangedWeaponEffect weaponEffect);

    void removeRangedWeaponEffect(int index);

    List<IRangedWeaponEffect> getRangedEffects();

    Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot);
}
