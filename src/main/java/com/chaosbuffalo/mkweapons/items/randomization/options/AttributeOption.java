package com.chaosbuffalo.mkweapons.items.randomization.options;

import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.capabilities.WeaponsCapabilities;
import com.chaosbuffalo.mkweapons.items.armor.IMKArmor;
import com.chaosbuffalo.mkweapons.items.effects.armor.ArmorModifierEffect;
import com.chaosbuffalo.mkweapons.items.effects.melee.MeleeModifierEffect;
import com.chaosbuffalo.mkweapons.items.effects.ranged.RangedModifierEffect;
import com.chaosbuffalo.mkweapons.items.randomization.slots.IRandomizationSlot;
import com.chaosbuffalo.mkweapons.items.randomization.slots.LootSlot;
import com.chaosbuffalo.mkweapons.items.randomization.slots.RandomizationSlotManager;
import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import com.chaosbuffalo.mkweapons.items.weapon.IMKRangedWeapon;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class AttributeOption extends BaseRandomizationOption {

    private final List<AttributeOptionEntry> modifiers;
    public static final ResourceLocation NAME = new ResourceLocation(MKWeapons.MODID, "attributes");


    public AttributeOption(){
        this(RandomizationSlotManager.ATTRIBUTE_SLOT);
    }

    public AttributeOption(IRandomizationSlot slot){
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
        }
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops) {
//        CompoundNBT fakeCap = new CompoundNBT();
//        CompoundNBT weaponsCap = new CompoundNBT();
//        fakeCap.put(WeaponsCapabilities.MK_WEAPON_CAP_ID.toString(), weaponsCap);
//        ItemStack testStack = new ItemStack(MKWeaponsItems.WEAPONS.get(0), 1, fakeCap);
//        testStack.addEnchantment(Enchantments.FIRE_ASPECT, 2);
        return ops.mergeToMap(super.serialize(ops), ImmutableMap.of(
                ops.createString("modifiers"),
                ops.createList(modifiers.stream().map(mod -> mod.serialize(ops)))
//                ops.createString("testItem"),
//                SerializationUtils.serializeItemStack(ops, testStack)
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
//        ItemStack test = dynamic.get("testItem").get().result().map(SerializationUtils::deserializeItemStack)
//                .orElse(ItemStack.EMPTY);
//        MKWeapons.LOGGER.info("Test item {} ", test);
    }


}
