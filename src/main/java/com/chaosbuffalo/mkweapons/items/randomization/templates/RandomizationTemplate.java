package com.chaosbuffalo.mkweapons.items.randomization.templates;

import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.randomization.slots.IRandomizationSlot;
import com.chaosbuffalo.mkweapons.items.randomization.slots.LootSlot;
import com.chaosbuffalo.mkweapons.items.randomization.slots.LootSlotManager;
import com.chaosbuffalo.mkweapons.items.randomization.slots.RandomizationSlotManager;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RandomizationTemplate{
    public static final ResourceLocation INVALID_TEMPLATE = new ResourceLocation(MKWeapons.MODID, "ranomization_template.error");
    private final ResourceLocation name;
    private final List<IRandomizationSlot> slots;
    private LootSlot lootSlot;

    public RandomizationTemplate(ResourceLocation name){
        this.name = name;
        this.slots = new ArrayList<>();
        this.lootSlot = LootSlotManager.INVALID;
    }

    public LootSlot getLootSlot() {
        return lootSlot;
    }

    public RandomizationTemplate(ResourceLocation name, LootSlot slot, IRandomizationSlot... slots){
        this(name);
        this.slots.addAll(Arrays.asList(slots));
        this.lootSlot = slot;
    }

    public ResourceLocation getName() {
        return name;
    }

    public List<IRandomizationSlot> getRandomizationSlots() {
        return slots;
    }

    public <D> D serialize(DynamicOps<D> ops) {
        return ops.createMap(ImmutableMap.of(
                ops.createString("templateType"), ops.createString(getName().toString()),
                ops.createString("lootSlot"), ops.createString(lootSlot.getName().toString()),
                ops.createString("slots"), ops.createList(slots.stream()
                        .map(entry -> ops.createString(entry.getName().toString())
                ))));
    }

    public <D> void deserialize(Dynamic<D> dynamic) {
        List<IRandomizationSlot> entries = dynamic.get("slots").asList(d -> {
            ResourceLocation slotName = new ResourceLocation(d.asString("mkweapons:randomization_slot.error"));
            return RandomizationSlotManager.getSlotFromName(slotName);
        });
        slots.clear();
        for (IRandomizationSlot entry : entries){
            if (entry != null){
                slots.add(entry);
            } else {
                MKWeapons.LOGGER.error("Failed to decode slot for randomization template of type {}", getName());
            }
        }
        lootSlot = LootSlotManager.getSlotFromName(dynamic.get("lootSlot").asString().result().map(ResourceLocation::new).orElse(LootSlotManager.INVALID_LOOT_SLOT));
    }

    public static <D> ResourceLocation readType(Dynamic<D> dynamic){
        return new ResourceLocation(dynamic.get("templateType").asString(INVALID_TEMPLATE.toString()));
    }

    public static <D> RandomizationTemplate deserializeTemplate(Dynamic<D> dynamic){
        ResourceLocation type = readType(dynamic);
        RandomizationTemplate template = new RandomizationTemplate(type);
        template.deserialize(dynamic);
        return template;
    }
}
