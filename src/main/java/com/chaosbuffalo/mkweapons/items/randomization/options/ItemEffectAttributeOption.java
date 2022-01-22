package com.chaosbuffalo.mkweapons.items.randomization.options;

import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.capabilities.WeaponsCapabilities;
import com.chaosbuffalo.mkweapons.items.accessories.MKAccessory;
import com.chaosbuffalo.mkweapons.items.armor.IMKArmor;
import com.chaosbuffalo.mkweapons.items.effects.accesory.AccessoryModifierEffect;
import com.chaosbuffalo.mkweapons.items.effects.armor.ArmorModifierEffect;
import com.chaosbuffalo.mkweapons.items.randomization.slots.IRandomizationSlot;
import com.chaosbuffalo.mkweapons.items.randomization.slots.LootSlot;
import com.chaosbuffalo.mkweapons.items.randomization.slots.RandomizationSlotManager;
import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import com.chaosbuffalo.mkweapons.items.weapon.IMKRangedWeapon;
import com.chaosbuffalo.mkweapons.items.effects.melee.MeleeModifierEffect;
import com.chaosbuffalo.mkweapons.items.effects.ranged.RangedModifierEffect;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ItemEffectAttributeOption extends BaseRandomizationOption {

    private final List<AttributeOptionEntry> modifiers;
    public static final ResourceLocation NAME = new ResourceLocation(MKWeapons.MODID, "effect_attributes");


    public ItemEffectAttributeOption(){
        this(RandomizationSlotManager.ATTRIBUTE_SLOT);
    }

    public ItemEffectAttributeOption(IRandomizationSlot slot){
        super(NAME, slot);
        modifiers = new ArrayList<>();
    }

    public void addAttributeModifier(Attribute attribute, AttributeModifier attributeModifier){
        modifiers.add(new AttributeOptionEntry(attribute, attributeModifier));
    }

    @Override
    public void applyToItemStackForSlot(ItemStack stack, LootSlot slot) {
        if (stack.getItem() instanceof IMKMeleeWeapon){
            stack.getCapability(WeaponsCapabilities.WEAPON_DATA_CAPABILITY).ifPresent(
                    cap -> cap.addMeleeWeaponEffect(new MeleeModifierEffect(modifiers)));
        } else if (stack.getItem() instanceof IMKRangedWeapon){
            stack.getCapability(WeaponsCapabilities.WEAPON_DATA_CAPABILITY).ifPresent(
                    cap -> cap.addRangedWeaponEffect(new RangedModifierEffect(modifiers)));
        } else if (stack.getItem() instanceof IMKArmor){
            stack.getCapability(WeaponsCapabilities.ARMOR_DATA_CAPABILITY).ifPresent(
                    cap -> cap.addArmorEffect(new ArmorModifierEffect(modifiers))
            );
        } else if (stack.getItem() instanceof MKAccessory){
            MKAccessory.getAccessoryHandler(stack).ifPresent(
                    cap -> cap.addEffect(new AccessoryModifierEffect(modifiers))
            );
        }
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops) {
        return ops.mergeToMap(super.serialize(ops), ImmutableMap.of(
                ops.createString("modifiers"),
                ops.createList(modifiers.stream().map(mod -> mod.serialize(ops)))
        )).result().orElse(ops.createMap(ImmutableMap.of()));
    }

    @Override
    public <D> void deserialize(Dynamic<D> dynamic) {
        super.deserialize(dynamic);
        List<AttributeOptionEntry> deserialized = dynamic.get("modifiers").asList(dyn -> {
            AttributeOptionEntry entry = new AttributeOptionEntry();
            entry.deserialize(dyn);
            return entry;
        });
        modifiers.clear();
        for (AttributeOptionEntry mod : deserialized){
            if (mod != null){
                modifiers.add(mod);
            }
        }
    }


}
