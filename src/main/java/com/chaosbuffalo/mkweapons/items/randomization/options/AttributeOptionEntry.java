package com.chaosbuffalo.mkweapons.items.randomization.options;

import com.chaosbuffalo.mkweapons.MKWeapons;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;
import java.util.UUID;

public class AttributeOptionEntry {
    private AttributeModifier modifier;
    private Attribute attribute;

    public AttributeOptionEntry(Attribute attribute, AttributeModifier modifier){
        this.modifier = modifier;
        this.attribute = attribute;
    }

    public AttributeOptionEntry(){

    }

    public AttributeModifier getModifier() {
        return modifier;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public AttributeOptionEntry copy(){
        return new AttributeOptionEntry(getAttribute(), new AttributeModifier(UUID.randomUUID(), modifier.getName(), modifier.getAmount(), modifier.getOperation()));
    }

    public <D> D serialize(DynamicOps<D> ops) {
        ImmutableMap.Builder<D, D> builder = ImmutableMap.builder();
        builder.put(ops.createString("Name"), ops.createString(modifier.getName()));
        builder.put(ops.createString("Amount"), ops.createDouble(modifier.getAmount()));
        builder.put(ops.createString("Operation"), ops.createInt(modifier.getOperation().getId()));
        builder.put(ops.createString("AttributeName"), ops.createString(attribute.getRegistryName().toString()));
        if (!modifier.getID().equals(Util.DUMMY_UUID)){
            builder.put(ops.createString("UUID"), ops.createString(modifier.getID().toString()));
        }
        return ops.createMap(builder.build());
    }


    private String getTranslationKeyForModifier(AttributeModifier.Operation op){
        switch (op){
            case MULTIPLY_BASE:
                return "mkweapons.modifier.description.percentage_base";
            case MULTIPLY_TOTAL:
                return "mkweapons.modifier.description.percentage_total";
            case ADDITION:
            default:
                return "mkweapons.modifier.description.addition";
        }
    }

    public ITextComponent getDescription(){
        String translationKey = getTranslationKeyForModifier(modifier.getOperation());
        double amount = modifier.getAmount();
        if (modifier.getOperation() != AttributeModifier.Operation.ADDITION){
            amount *= 100.0f;
        }
        return new TranslationTextComponent(translationKey, I18n.format(attribute.getAttributeName()), amount).mergeStyle(TextFormatting.GRAY);
    }

    public <D> void deserialize(Dynamic<D> dynamic){
        Optional<String> name = dynamic.get("Name").asString().result();
        UUID uuid = dynamic.get("UUID").asString().result().map(UUID::fromString).orElse(Util.DUMMY_UUID);
        double amount = dynamic.get("Amount").asDouble(0.0);
        int op = dynamic.get("Operation").asInt(0);
        if (name.isPresent()){
            modifier = new AttributeModifier(uuid, name.get(), amount, AttributeModifier.Operation.byId(op));
        } else {
            MKWeapons.LOGGER.error("Failed to decode attribute modifier {} : {}", name, dynamic);
        }
        Optional<String> attributeName = dynamic.get("AttributeName").asString().result();
        if (attributeName.isPresent()){
            this.attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(attributeName.get()));
        } else {
            MKWeapons.LOGGER.error("Failed to decode attribute name {}", dynamic);
        }
    }

}
