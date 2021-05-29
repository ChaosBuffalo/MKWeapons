package com.chaosbuffalo.mkweapons.items.randomization.templates;

public class RandomizationTemplateEntry {
    public final RandomizationTemplate template;
    public final double weight;

    public RandomizationTemplateEntry(RandomizationTemplate template, double weight){
        this.template = template;
        this.weight = weight;
    }
}
