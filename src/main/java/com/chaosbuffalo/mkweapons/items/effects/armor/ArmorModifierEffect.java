package com.chaosbuffalo.mkweapons.items.effects.armor;

import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.effects.ItemModifierEffect;
import com.chaosbuffalo.mkweapons.items.randomization.options.AttributeOptionEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class ArmorModifierEffect extends ItemModifierEffect implements IArmorEffect {
    public static final ResourceLocation NAME = new ResourceLocation(MKWeapons.MODID, "weapon_effect.armor_modifier");

    public ArmorModifierEffect(List<AttributeOptionEntry> modifiers) {
        this();
        this.modifiers.addAll(modifiers);
    }

    public ArmorModifierEffect() {
        super(NAME, TextFormatting.WHITE);
    }
}
