package com.chaosbuffalo.mkweapons.items.weapon.effects;

import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.randomization.options.AttributeOptionEntry;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ModifierWeaponEffect extends BaseWeaponEffect {
    protected final List<AttributeOptionEntry> modifiers;

    public ModifierWeaponEffect(ResourceLocation name, TextFormatting color) {
        super(name, color);
        modifiers = new ArrayList<>();
    }


    @Override
    public <D> D serialize(DynamicOps<D> ops) {
        return ops.mergeToMap(super.serialize(ops), ImmutableMap.of(
                ops.createString("modifiers"),
                ops.createList(modifiers.stream().map(mod -> mod.serialize(ops)))
        )).result().orElse(ops.createMap(ImmutableMap.of()));
    }

    public void addAttributeModifier(Attribute attribute, AttributeModifier attributeModifier){
        modifiers.add(new AttributeOptionEntry(attribute, attributeModifier));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        for (AttributeOptionEntry entry : modifiers){
            tooltip.add(entry.getDescription());
        }
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

    @Override
    public void onEntityEquip(LivingEntity entity) {
        for (AttributeOptionEntry entry : modifiers){
            ModifiableAttributeInstance attr = entity.getAttribute(entry.getAttribute());
            if (attr != null){
                attr.applyNonPersistentModifier(entry.getModifier());
            }
        }
    }

    @Override
    public void onEntityUnequip(LivingEntity entity) {
        for (AttributeOptionEntry entry : modifiers){
            ModifiableAttributeInstance attr = entity.getAttribute(entry.getAttribute());
            if (attr != null) {
                attr.removeModifier(entry.getModifier());
            }
        }
    }
}
