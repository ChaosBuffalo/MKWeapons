package com.chaosbuffalo.mkweapons.capabilities;

import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.effects.IItemEffect;
import com.chaosbuffalo.mkweapons.items.effects.ItemEffects;
import com.chaosbuffalo.mkweapons.items.effects.armor.IArmorEffect;
import com.mojang.serialization.Dynamic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ArmorDataHandler implements IArmorData {

    private final ItemStack itemStack;
    private final List<IArmorEffect> armorEffects;

    public ArmorDataHandler(ItemStack item) {
        itemStack = item;
        armorEffects = new ArrayList<>();
    }

    @Override
    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public void forEachEffect(Consumer<IArmorEffect> effectConsumer) {
        armorEffects.forEach(effectConsumer);
    }

    @Override
    public void addArmorEffect(IArmorEffect armorEffect) {
        armorEffects.add(armorEffect);
    }

    @Override
    public void removeArmorEffect(int index) {
        armorEffects.remove(index);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        ListNBT effectList = new ListNBT();
        forEachEffect(effect -> effectList.add(effect.serialize(NBTDynamicOps.INSTANCE)));
        nbt.put("armor_effects", effectList);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("armor_effects")) {
            ListNBT effectList = nbt.getList("armor_effects", Constants.NBT.TAG_COMPOUND);
            for (INBT effectNBT : effectList) {
                IItemEffect effect = ItemEffects.deserializeEffect(new Dynamic<>(NBTDynamicOps.INSTANCE, effectNBT));
                if (effect instanceof IArmorEffect) {
                    addArmorEffect((IArmorEffect) effect);
                } else {
                    MKWeapons.LOGGER.error("Failed to deserialize armor effect {} for item {}", effectNBT,
                            getItemStack());
                }
            }
        }
    }
}
