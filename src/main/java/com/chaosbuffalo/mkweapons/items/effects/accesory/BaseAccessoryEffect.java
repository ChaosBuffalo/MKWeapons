package com.chaosbuffalo.mkweapons.items.effects.accesory;

import com.chaosbuffalo.mkweapons.items.effects.BaseItemEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public abstract class BaseAccessoryEffect extends BaseItemEffect implements IAccessoryEffect {

    public BaseAccessoryEffect(ResourceLocation name, TextFormatting color) {
        super(name, color);
    }
}
