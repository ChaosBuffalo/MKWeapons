package com.chaosbuffalo.mkweapons.items.weapon.effects.melee;

import com.chaosbuffalo.mkweapons.items.weapon.effects.BaseWeaponEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public abstract class BaseMeleeWeaponEffect extends BaseWeaponEffect implements IMeleeWeaponEffect {

    public BaseMeleeWeaponEffect(ResourceLocation name, TextFormatting color) {
        super(name, color);
    }
}
