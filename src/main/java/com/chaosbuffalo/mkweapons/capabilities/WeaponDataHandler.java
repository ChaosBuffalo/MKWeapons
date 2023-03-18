package com.chaosbuffalo.mkweapons.capabilities;

import com.chaosbuffalo.mkcore.MKCoreRegistry;
import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.effects.IItemEffect;
import com.chaosbuffalo.mkweapons.items.effects.ItemEffects;
import com.chaosbuffalo.mkweapons.items.effects.melee.IMeleeWeaponEffect;
import com.chaosbuffalo.mkweapons.items.effects.ranged.IRangedWeaponEffect;
import com.mojang.serialization.Dynamic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class WeaponDataHandler implements IWeaponData {

    private final ItemStack itemStack;

    private ResourceLocation ability;

    public WeaponDataHandler(ItemStack stack) {
        itemStack = stack;
        ability = MKCoreRegistry.INVALID_ABILITY;
    }

    @Override
    public ItemStack getItemStack() {
        return itemStack;
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
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        ResourceLocation abilityId = getAbilityName() != null ? getAbilityName() : MKCoreRegistry.INVALID_ABILITY;
        nbt.putString("ability_granted", abilityId.toString());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        ResourceLocation abilityId = new ResourceLocation(nbt.getString("ability_granted"));
        if (!abilityId.equals(MKCoreRegistry.INVALID_ABILITY)) {
            setAbilityId(abilityId);
        }
    }

    public static class Melee extends WeaponDataHandler implements IMeleeWeaponData {
        private final List<IMeleeWeaponEffect> meleeWeaponEffects;

        public Melee(ItemStack stack) {
            super(stack);
            meleeWeaponEffects = new ArrayList<>();
        }

        @Override
        public boolean hasMeleeWeaponEffects() {
            return !meleeWeaponEffects.isEmpty();
        }

        @Override
        public void forEachMeleeEffect(Consumer<? super IMeleeWeaponEffect> consumer) {
            meleeWeaponEffects.forEach(consumer);
        }

        @Override
        public List<IMeleeWeaponEffect> getMeleeEffects() {
            return meleeWeaponEffects;
        }

        @Override
        public void addMeleeWeaponEffect(IMeleeWeaponEffect weaponEffect) {
            meleeWeaponEffects.add(weaponEffect);
        }

        @Override
        public void removeMeleeWeaponEffect(int index) {
            meleeWeaponEffects.remove(index);
        }

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT tag = super.serializeNBT();
            ListNBT effectList = new ListNBT();
            for (IMeleeWeaponEffect effect : meleeWeaponEffects) {
                effectList.add(effect.serialize(NBTDynamicOps.INSTANCE));
            }
            tag.put("melee_effects", effectList);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            super.deserializeNBT(nbt);
            if (nbt.contains("melee_effects")) {
                ListNBT effectList = nbt.getList("melee_effects", Constants.NBT.TAG_COMPOUND);
                for (INBT effectNBT : effectList) {
                    IItemEffect effect = ItemEffects.deserializeEffect(new Dynamic<>(NBTDynamicOps.INSTANCE, effectNBT));
                    if (effect instanceof IMeleeWeaponEffect) {
                        addMeleeWeaponEffect((IMeleeWeaponEffect) effect);
                    } else {
                        MKWeapons.LOGGER.error("Failed to deserialize melee effect {} for item {}", effectNBT,
                                getItemStack());
                    }
                }
            }
        }
    }

    public static class Ranged extends WeaponDataHandler implements IRangedWeaponData {

        private final List<IRangedWeaponEffect> rangedWeaponEffects;

        public Ranged(ItemStack stack) {
            super(stack);
            rangedWeaponEffects = new ArrayList<>();
        }

        @Override
        public boolean hasRangedWeaponEffects() {
            return !rangedWeaponEffects.isEmpty();
        }

        @Override
        public void addRangedWeaponEffect(IRangedWeaponEffect weaponEffect) {
            rangedWeaponEffects.add(weaponEffect);
        }

        @Override
        public void removeRangedWeaponEffect(int index) {
            rangedWeaponEffects.remove(index);
        }

        @Override
        public void forEachRangedEffect(Consumer<? super IRangedWeaponEffect> consumer) {
            rangedWeaponEffects.forEach(consumer);
        }

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT tag = super.serializeNBT();
            ListNBT rangedEffectList = new ListNBT();
            for (IRangedWeaponEffect effect : rangedWeaponEffects) {
                rangedEffectList.add(effect.serialize(NBTDynamicOps.INSTANCE));
            }
            tag.put("ranged_effects", rangedEffectList);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            super.deserializeNBT(nbt);
            if (nbt.contains("ranged_effects")) {
                ListNBT rangedEffectList = nbt.getList("ranged_effects", Constants.NBT.TAG_COMPOUND);
                for (INBT effectNBT : rangedEffectList) {
                    IItemEffect effect = ItemEffects.deserializeEffect(new Dynamic<>(NBTDynamicOps.INSTANCE, effectNBT));
                    if (effect instanceof IRangedWeaponEffect) {
                        addRangedWeaponEffect((IRangedWeaponEffect) effect);
                    } else {
                        MKWeapons.LOGGER.error("Failed to deserialize ranged effect {} for item {}", effectNBT,
                                getItemStack());
                    }
                }
            }
        }
    }

}
