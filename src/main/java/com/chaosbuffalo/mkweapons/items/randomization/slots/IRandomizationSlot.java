package com.chaosbuffalo.mkweapons.items.randomization.slots;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public interface IRandomizationSlot {

    ResourceLocation getName();

    ITextComponent getDisplayName();
}
