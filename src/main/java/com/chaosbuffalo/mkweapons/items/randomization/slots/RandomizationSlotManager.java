package com.chaosbuffalo.mkweapons.items.randomization.slots;

import com.chaosbuffalo.mkweapons.MKWeapons;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.util.HashMap;
import java.util.Map;

public class RandomizationSlotManager {

    public static final ResourceLocation INVALID_SLOT = new ResourceLocation(MKWeapons.MODID,
            "randomization.invalid");

    public static final IRandomizationSlot ATTRIBUTE_SLOT = new RandomizationSlot(
            new ResourceLocation(MKWeapons.MODID, "randomization.attributes"), TextFormatting.DARK_GREEN);

    public static final Map<ResourceLocation, IRandomizationSlot> SLOTS = new HashMap<>();

    public static void addRandomizatiotSlot(IRandomizationSlot slot){
        SLOTS.put(slot.getName(), slot);
    }

    public static IRandomizationSlot getSlotFromName(ResourceLocation name){
        return SLOTS.get(name);
    }

    public static void setupRandomizationSlots(){
        addRandomizatiotSlot(ATTRIBUTE_SLOT);
    }
}
