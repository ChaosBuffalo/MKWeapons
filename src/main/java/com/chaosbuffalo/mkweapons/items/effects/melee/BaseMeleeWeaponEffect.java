package com.chaosbuffalo.mkweapons.items.effects.melee;

import com.chaosbuffalo.mkweapons.items.effects.BaseItemEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public abstract class BaseMeleeWeaponEffect extends BaseItemEffect implements IMeleeWeaponEffect {

    public BaseMeleeWeaponEffect(ResourceLocation name, TextFormatting color) {
        super(name, color);
    }
}
