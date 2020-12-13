package com.chaosbuffalo.mkweapons.items.randomization.slots;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class RandomizationSlot implements IRandomizationSlot {
    private final ResourceLocation name;
    private final TextFormatting textColor;

    public RandomizationSlot(ResourceLocation name, TextFormatting textColor){
        this.name = name;
        this.textColor = textColor;
    }

    @Override
    public ResourceLocation getName() {
        return name;
    }

    public TextFormatting getTextColor() {
        return textColor;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(String.format("%s.randomization_slot.%s.name",
                getName().getNamespace(), getName().getPath())).mergeStyle(getTextColor());
    }
}
