package com.chaosbuffalo.mkweapons.items.randomization.slots;

import com.chaosbuffalo.mkweapons.MKWeapons;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class LootSlotManager {

    public static final ResourceLocation INVALID_LOOT_SLOT = new ResourceLocation(MKWeapons.MODID,
            "loot_slot.invalid");

    public static final LootSlot MAIN_HAND = new LootSlot(new ResourceLocation(MKWeapons.MODID, "main_hand"),
            EquipmentSlotType.MAINHAND);
    public static final LootSlot OFF_HAND = new LootSlot(new ResourceLocation(MKWeapons.MODID, "off_hand"),
            EquipmentSlotType.OFFHAND);
    public static final LootSlot CHEST = new LootSlot(new ResourceLocation(MKWeapons.MODID, "chest"),
            EquipmentSlotType.CHEST);
    public static final LootSlot LEGS = new LootSlot(new ResourceLocation(MKWeapons.MODID, "legs"),
            EquipmentSlotType.LEGS);
    public static final LootSlot HEAD = new LootSlot(new ResourceLocation(MKWeapons.MODID, "head"),
            EquipmentSlotType.HEAD);
    public static final LootSlot FEET = new LootSlot(new ResourceLocation(MKWeapons.MODID, "feet"),
            EquipmentSlotType.FEET);
    public static final LootSlot INVALID = new LootSlot(INVALID_LOOT_SLOT, (ent) -> ItemStack.EMPTY,
            (ent, item) -> {}, ((attributeModifier, s, itemStack) -> {}));
    public static final LootSlot ITEMS = new LootSlot(new ResourceLocation(MKWeapons.MODID, "items"),
            (ent) -> ItemStack.EMPTY, (ent, item) -> {}, ((attributeModifier, s, itemStack) -> {}));

    public static final Map<ResourceLocation, LootSlot> SLOTS = new HashMap<>();

    public static void addLootSlot(LootSlot slot){
        SLOTS.put(slot.getName(), slot);
    }

    public static LootSlot getSlotFromName(ResourceLocation name){
        return SLOTS.get(name);
    }

    public static void setupLootSlots(){
        addLootSlot(MAIN_HAND);
        addLootSlot(OFF_HAND);
        addLootSlot(CHEST);
        addLootSlot(LEGS);
        addLootSlot(HEAD);
        addLootSlot(FEET);
        addLootSlot(INVALID);
        addLootSlot(ITEMS);
    }



}
