package com.chaosbuffalo.mkweapons.items.effects.ranged;

import com.chaosbuffalo.mkweapons.items.effects.BaseItemEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public abstract class BaseRangedWeaponEffect extends BaseItemEffect implements IRangedWeaponEffect {
    public BaseRangedWeaponEffect(ResourceLocation name, TextFormatting color) {
        super(name, color);
    }
}
