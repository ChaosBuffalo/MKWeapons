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
    private final Function<LivingEntity, ItemStack> slotGetter;
    private final BiConsumer<LivingEntity, ItemStack> slotSetter;
    private final TriConsumer<AttributeModifier, Attribute, ItemStack> addAttributeModifierToStack;

    public LootSlot(ResourceLocation name, Function<LivingEntity, ItemStack> slotGetter,
                    BiConsumer<LivingEntity, ItemStack> slotSetter,
                    TriConsumer<AttributeModifier, Attribute, ItemStack> addAttributeModifierToStack){
        this.name = name;
        this.slotGetter = slotGetter;
        this.slotSetter = slotSetter;
        this.addAttributeModifierToStack = addAttributeModifierToStack;
    }

    public LootSlot(ResourceLocation name, EquipmentSlotType slotType){
        this(name, (entity) -> entity.getItemStackFromSlot(slotType),
                (entity, itemStack) -> entity.setItemStackToSlot(slotType, itemStack),
                (attributeModifier, attr, itemStack) ->
                        itemStack.addAttributeModifier(attr, attributeModifier, slotType));
    }

    public ResourceLocation getName() {
        return name;
    }

    public void setItemInSlot(LivingEntity entity, ItemStack item){
        slotSetter.accept(entity, item);
    }

    public ItemStack getItemInSlot(LivingEntity entity){
        return slotGetter.apply(entity);
    }

    public void addAttributeModifierForSlotToItem(AttributeModifier modifier, Attribute attr, ItemStack itemStack){
        addAttributeModifierToStack.accept(modifier, attr, itemStack);
    }
}
