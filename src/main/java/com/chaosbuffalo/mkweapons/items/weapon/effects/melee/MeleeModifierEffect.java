package com.chaosbuffalo.mkweapons.items.weapon.effects.melee;

import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.randomization.options.AttributeOptionEntry;
import com.chaosbuffalo.mkweapons.items.weapon.effects.ModifierWeaponEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class MeleeModifierEffect extends ModifierWeaponEffect implements IMeleeWeaponEffect {
    public static final ResourceLocation NAME = new ResourceLocation(MKWeapons.MODID, "weapon_effect.melee_modifier");

    public MeleeModifierEffect(List<AttributeOptionEntry> modifiers) {
        this();
        this.modifiers.addAll(modifiers);
    }

    public MeleeModifierEffect(){
        super(NAME, TextFormatting.WHITE);
    }
}
