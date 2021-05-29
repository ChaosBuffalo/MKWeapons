package com.chaosbuffalo.mkweapons.items.randomization.options;

import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.randomization.slots.IRandomizationSlot;
import com.chaosbuffalo.mkweapons.items.randomization.slots.LootSlot;
import com.chaosbuffalo.mkweapons.items.randomization.slots.LootSlotManager;
import com.chaosbuffalo.mkweapons.items.randomization.slots.RandomizationSlotManager;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class BaseRandomizationOption implements IRandomizationOption{
    public static final ResourceLocation INVALID_OPTION = new ResourceLocation(MKWeapons.MODID, "randomization_option.error");
    private final ResourceLocation typeName;
    private final Set<LootSlot> applicableSlots;
    private IRandomizationSlot slot;
    private double weight;

    public BaseRandomizationOption(ResourceLocation typeName, IRandomizationSlot slot){
        this(typeName, slot, 1.0);
    }

    public BaseRandomizationOption(ResourceLocation typeName, IRandomizationSlot slot, double weight){
        this(typeName);
        this.slot = slot;
        this.weight = weight;
    }

    public BaseRandomizationOption(ResourceLocation typeName){
        this.typeName = typeName;
        this.applicableSlots = new HashSet<>();
    }

    @Override
    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public boolean isApplicableToItem(ItemStack stack) {
        return true;
    }

    @Override
    public void setSlot(IRandomizationSlot slot) {
        this.slot = slot;
    }

    @Override
    public void addApplicableSlot(LootSlot slot){
        applicableSlots.add(slot);
    }

    @Override
    public IRandomizationSlot getSlot() {
        return slot;
    }

    @Override
    public ResourceLocation getName() {
        return typeName;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public <D> void deserialize(Dynamic<D> dynamic) {
        List<String> slotNames = dynamic.get("slots").asList(d ->
                d.asString(LootSlotManager.INVALID_LOOT_SLOT.toString()));
        applicableSlots.clear();
        for (String slotName : slotNames){
            if (slotName != null){
                ResourceLocation slotLoc = new ResourceLocation(slotName);
                LootSlot slot = LootSlotManager.getSlotFromName(slotLoc);
                if (slot != null){
                    applicableSlots.add(slot);
                }
            }
        }
        String slotName = dynamic.get("slotName").asString(RandomizationSlotManager.INVALID_SLOT.toString());
        ResourceLocation slotNameRL = new ResourceLocation(slotName);
        slot = RandomizationSlotManager.getSlotFromName(slotNameRL);
        weight = dynamic.get("weight").asDouble(1.0);
    }

    @Override
    public Set<LootSlot> getApplicableSlots() {
        return applicableSlots;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops) {
        return ops.createMap(ImmutableMap.of(
                ops.createString("optionType"), ops.createString(getName().toString()),
                ops.createString("slotName"), ops.createString(getSlot().getName().toString()),
                ops.createString("slots"), ops.createList(applicableSlots.stream().map(x ->
                        ops.createString(x.getName().toString()))),
                ops.createString("weight"), ops.createDouble(getWeight())
        ));
    }

    public static <D> ResourceLocation readType(Dynamic<D> dynamic){
        return new ResourceLocation(dynamic.get("optionType").asString(INVALID_OPTION.toString()));
    }
}
