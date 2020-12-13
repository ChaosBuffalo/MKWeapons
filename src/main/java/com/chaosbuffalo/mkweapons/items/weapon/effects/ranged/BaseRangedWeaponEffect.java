package com.chaosbuffalo.mkweapons.items.weapon.effects.ranged;

import com.chaosbuffalo.mkweapons.items.weapon.effects.BaseWeaponEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public abstract class BaseRangedWeaponEffect extends BaseWeaponEffect implements IRangedWeaponEffect {
    public BaseRangedWeaponEffect(ResourceLocation name, TextFormatting color) {
        super(name, color);
    }
}
