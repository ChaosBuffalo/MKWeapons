package com.chaosbuffalo.mkweapons.items.effects.armor;

import com.chaosbuffalo.mkweapons.items.effects.BaseItemEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public abstract class BaseArmorEffect extends BaseItemEffect implements IArmorEffect {
    public BaseArmorEffect(ResourceLocation name, TextFormatting color) {
        super(name, color);
    }
}
