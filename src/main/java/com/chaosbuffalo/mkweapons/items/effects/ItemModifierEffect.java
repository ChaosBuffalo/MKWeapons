package com.chaosbuffalo.mkweapons.items.effects;

import com.chaosbuffalo.mkweapons.items.randomization.options.AttributeOptionEntry;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemModifierEffect extends BaseItemEffect {
    protected final List<AttributeOptionEntry> modifiers;

    public ItemModifierEffect(ResourceLocation name, TextFormatting color) {
        super(name, color);
        modifiers = new ArrayList<>();
    }


    @Override
    public <D> void writeAdditionalData(DynamicOps<D> ops, ImmutableMap.Builder<D, D> builder) {
        super.writeAdditionalData(ops, builder);
        builder.put(ops.createString("modifiers"),
                ops.createList(modifiers.stream().map(mod -> mod.serialize(ops))));
    }

    public void addAttributeModifier(Attribute attribute, AttributeModifier attributeModifier) {
        modifiers.add(new AttributeOptionEntry(attribute, attributeModifier, attributeModifier.getAmount(), attributeModifier.getAmount()));
    }

    public List<AttributeOptionEntry> getModifiers() {
        return modifiers;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {

    }

    @Override
    public <D> void readAdditionalData(Dynamic<D> dynamic) {
        super.readAdditionalData(dynamic);
        List<AttributeOptionEntry> deserialized = dynamic.get("modifiers").asList(dyn -> {
            AttributeOptionEntry entry = new AttributeOptionEntry();
            entry.deserialize(dyn);
            return entry;
        });
        modifiers.clear();
        for (AttributeOptionEntry mod : deserialized) {
            if (mod != null) {
                modifiers.add(mod);
            }
        }
    }
}
