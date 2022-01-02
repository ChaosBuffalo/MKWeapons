package com.chaosbuffalo.mkweapons.capabilities;

import com.chaosbuffalo.mkweapons.items.effects.melee.IMeleeWeaponEffect;
import com.chaosbuffalo.mkweapons.items.effects.ranged.IRangedWeaponEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.List;

public interface IWeaponData extends INBTSerializable<CompoundNBT> {

    void attach(ItemStack itemStack);

    ItemStack getItemStack();

    List<IMeleeWeaponEffect> getMeleeWeaponEffects();

    List<IMeleeWeaponEffect> getCachedMeleeWeaponEffects();

    ResourceLocation getAbilityName();

    void setAbilityId(ResourceLocation ability);

    boolean hasMeleeWeaponEffects();

    void markCacheDirty();

    void addMeleeWeaponEffect(IMeleeWeaponEffect weaponEffect);

    void removeMeleeWeaponEffect(int index);

    boolean hasRangedWeaponEffects();

    void addRangedWeaponEffect(IRangedWeaponEffect weaponEffect);

    void removeRangedWeaponEffect(int index);

    List<IRangedWeaponEffect> getRangedWeaponEffects();

    List<IRangedWeaponEffect> getCachedRangedWeaponEffects();
}
