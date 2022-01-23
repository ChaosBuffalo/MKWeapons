package com.chaosbuffalo.mkweapons.items.randomization.slots;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class LootSlot {
    private final ResourceLocation name;
    private final BiConsumer<LivingEntity, ItemStack> slotSetter;

    public LootSlot(ResourceLocation name, BiConsumer<LivingEntity, ItemStack> slotSetter){
        this.name = name;
        this.slotSetter = slotSetter;
    }

    public LootSlot(ResourceLocation name, EquipmentSlotType slotType){
        this(name, (entity, itemStack) -> entity.setItemStackToSlot(slotType, itemStack));
    }

    public ResourceLocation getName() {
        return name;
    }

    public void setItemInSlot(LivingEntity entity, ItemStack item){
        slotSetter.accept(entity, item);
    }

}
