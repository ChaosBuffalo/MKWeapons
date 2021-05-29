package com.chaosbuffalo.mkweapons.items.weapon.effects.ranged;

import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.randomization.options.AttributeOptionEntry;
import com.chaosbuffalo.mkweapons.items.weapon.effects.ModifierWeaponEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class RangedModifierEffect extends ModifierWeaponEffect implements IRangedWeaponEffect {
    public static final ResourceLocation NAME = new ResourceLocation(MKWeapons.MODID, "weapon_effect.ranged_modifier");

    public RangedModifierEffect(List<AttributeOptionEntry> modifiers) {
        this();
        this.modifiers.addAll(modifiers);
    }

    public RangedModifierEffect(){
        super(NAME, TextFormatting.WHITE);
    }
}
