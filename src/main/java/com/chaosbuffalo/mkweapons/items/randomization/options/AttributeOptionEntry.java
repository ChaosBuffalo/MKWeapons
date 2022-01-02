package com.chaosbuffalo.mkweapons.items.randomization.options;

import com.chaosbuffalo.mkweapons.MKWeapons;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.ResourceLocation;
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

    public <D> D serialize(DynamicOps<D> ops){
        return ops.createMap(ImmutableMap.of(
                ops.createString("Name"), ops.createString(modifier.getName()),
                ops.createString("Amount"), ops.createDouble(modifier.getAmount()),
                ops.createString("Operation"), ops.createInt(modifier.getOperation().getId()),
                ops.createString("UUID"), ops.createString(modifier.getID().toString()),
                ops.createString("AttributeName"), ops.createString(attribute.getRegistryName().toString())
        ));
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
        Optional<String> uuidString = dynamic.get("UUID").asString().result();
        double amount = dynamic.get("Amount").asDouble(0.0);
        int op = dynamic.get("Operation").asInt(0);
        if (name.isPresent() && uuidString.isPresent()){
            UUID uuid = UUID.fromString(uuidString.get());
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
