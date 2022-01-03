package com.chaosbuffalo.mkweapons.items.randomization;

import com.chaosbuffalo.mkcore.utils.SerializationUtils;
import com.chaosbuffalo.mkweapons.items.randomization.options.IRandomizationOption;
import com.chaosbuffalo.mkweapons.items.randomization.options.RandomizationOptionManager;
import com.chaosbuffalo.mkweapons.items.randomization.slots.LootSlot;
import com.chaosbuffalo.mkweapons.items.randomization.slots.LootSlotManager;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class LootConstructor {

    private ItemStack item;
    private LootSlot slot;
    private List<IRandomizationOption> options;

    public LootConstructor(ItemStack item, LootSlot slot, List<IRandomizationOption> options){
        this.item = item;
        this.slot = slot;
        this.options = new ArrayList<>();
        this.options.addAll(options);
    }

    public LootConstructor(){
        this.item = null;
        this.slot = null;
        this.options = new ArrayList<>();
    }

    public ItemStack constructItem(){
        if (item.isEmpty() || slot.equals(LootSlotManager.INVALID)){
            return ItemStack.EMPTY;
        }
        ItemStack newItem = item.copy();
        for (IRandomizationOption option : options){
            option.applyToItemStackForSlot(newItem, slot);
        }
        return newItem;
    }

    public <D> D serialize(DynamicOps<D> ops){
        return ops.createMap(ImmutableMap.of(
                ops.createString("slot"), ops.createString(slot.getName().toString()),
                ops.createString("item"), SerializationUtils.serializeItemStack(ops, item),
                ops.createString("options"), ops.createList(options.stream().map(option -> option.serialize(ops)))
        ));
    }

    public <D> void deserialize(Dynamic<D> dynamic){
        List<IRandomizationOption> options = dynamic.get("options").asList(
                RandomizationOptionManager::deserializeOption);
        this.options.clear();
        for (IRandomizationOption option : options){
            if (option != null){
                this.options.add(option);
            }
        }
        item = dynamic.get("itemStack").map(SerializationUtils::deserializeItemStack).result().orElse(ItemStack.EMPTY);
        slot = dynamic.get("slot").asString().result().map(
                slotName -> LootSlotManager.getSlotFromName(new ResourceLocation(slotName)))
                .orElse(LootSlotManager.INVALID);

    }
}
