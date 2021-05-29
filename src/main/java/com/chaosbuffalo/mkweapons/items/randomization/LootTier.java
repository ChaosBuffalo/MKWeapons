package com.chaosbuffalo.mkweapons.items.randomization;

import com.chaosbuffalo.mkcore.utils.RandomCollection;
import com.chaosbuffalo.mkweapons.items.randomization.options.IRandomizationOption;
import com.chaosbuffalo.mkweapons.items.randomization.options.RandomizationOptionManager;
import com.chaosbuffalo.mkweapons.items.randomization.slots.IRandomizationSlot;
import com.chaosbuffalo.mkweapons.items.randomization.slots.LootSlot;
import com.chaosbuffalo.mkweapons.items.randomization.slots.LootSlotManager;
import com.chaosbuffalo.mkweapons.items.randomization.templates.RandomizationTemplate;
import com.chaosbuffalo.mkweapons.items.randomization.templates.RandomizationTemplateEntry;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class LootTier {
    private final ResourceLocation name;
    private final Map<LootSlot, List<Item>> potentialItemsForSlot;
    private final List<IRandomizationOption> options;
    private final List<RandomizationTemplateEntry> templates;

    public LootTier(ResourceLocation name){
        this.name = name;
        this.potentialItemsForSlot = new HashMap<>();
        this.options = new ArrayList<>();
        this.templates = new ArrayList<>();
    }

    @Nullable
    public Item chooseItemForSlot(LootSlot slot, Random random){
        List<Item> potentials = potentialItemsForSlot.get(slot);
        if (potentials.isEmpty()){
            return null;
        } else {
            return potentials.get(random.nextInt(potentials.size()));
        }
    }

    public RandomizationTemplate chooseTemplate(Random random){
        RandomCollection<RandomizationTemplate> choices = new RandomCollection<>();
        for (RandomizationTemplateEntry entry : templates){
            choices.add(entry.weight, entry.template);
        }
        return choices.next(random);
    }

    public LootConstructor generateConstructorForSlot(Random random, LootSlot slot){
        Item item = chooseItemForSlot(slot, random);
        RandomizationTemplate template = chooseTemplate(random);
        ItemStack stack = new ItemStack(item);
        List<IRandomizationOption> chosenOptions = new ArrayList<>();
        for (IRandomizationSlot randomizationSlot : template.getRandomizationSlots()){
            List<IRandomizationOption> options = this.options.stream().filter(x ->
                    x.getSlot().equals(randomizationSlot) && x.isApplicableToItem(stack)
                            && x.getApplicableSlots().contains(slot))
                    .collect(Collectors.toList());
            RandomCollection<IRandomizationOption> optionChoices = new RandomCollection<>();
            for (IRandomizationOption option : options){
                optionChoices.add(option.getWeight(), option);
            }
            if (optionChoices.size() > 0){
                chosenOptions.add(optionChoices.next(random));
            }
        }
        return new LootConstructor(item, slot, chosenOptions);
    }

    public void addItemToSlot(LootSlot slot, Item item){
        List<Item> slotItems = potentialItemsForSlot.computeIfAbsent(slot,
                (slotArg) -> new ArrayList<>());
        slotItems.add(item);
    }

    public void addTemplate(RandomizationTemplate template, double weight){
        this.templates.add(new RandomizationTemplateEntry(template, weight));
    }

    public ResourceLocation getName() {
        return name;
    }

    public void addRandomizationOption(IRandomizationOption option){
        options.add(option);
    }

    public <D> D serialize(DynamicOps<D> ops){
        return ops.createMap(ImmutableMap.of(
                ops.createString("slotItems"),
                ops.createMap(potentialItemsForSlot.entrySet().stream().map(lootSlotListEntry ->
                        Pair.of(lootSlotListEntry.getKey().getName(),
                                lootSlotListEntry.getValue().stream()
                                .map(x -> ops.createString(x.getRegistryName().toString()))
                                .collect(Collectors.toList()))
                ).collect(Collectors.toMap(pair -> ops.createString(pair.getFirst().toString()),
                        pair -> ops.createList(pair.getSecond().stream())))),
                ops.createString("options"),
                ops.createList(options.stream().map(option -> option.serialize(ops))),
                ops.createString("templates"),
                ops.createList(templates.stream().map(template -> ops.createMap(ImmutableMap.of(
                        ops.createString("template"), template.template.serialize(ops),
                        ops.createString("weight"), ops.createDouble(template.weight)
                ))))
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
        List<RandomizationTemplateEntry> templates = dynamic.get("templates").asList(d -> {
            double weight = d.get("weight").asDouble(1.0);
            RandomizationTemplate template = d.get("template").result()
                    .map(RandomizationTemplate::deserializeTemplate).orElse(null);
            if (template != null){
                return new RandomizationTemplateEntry(template, weight);
            } else {
                return null;
            }
        });
        this.templates.clear();
        for (RandomizationTemplateEntry template : templates){
            if (template != null){
                this.templates.add(template);
            }
        }
        Map<LootSlot, List<Item>> slotMap = dynamic.get("slotItems").asMap(
                (d -> d.asString().result().map(slotName -> LootSlotManager.getSlotFromName(new ResourceLocation(slotName)))
                                .orElse(LootSlotManager.INVALID)),
                (d -> d.asList(x -> x.asString().result().map(name ->
                        ForgeRegistries.ITEMS.getValue(new ResourceLocation(name))).orElse(null))));

        potentialItemsForSlot.clear();
        for (Map.Entry<LootSlot, List<Item>> entry : slotMap.entrySet()){
            if (entry.getKey() != LootSlotManager.INVALID){
                potentialItemsForSlot.put(entry.getKey(), entry.getValue().stream().filter(Objects::nonNull)
                        .collect(Collectors.toList()));
            }
        }
    }
}
