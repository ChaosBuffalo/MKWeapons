package com.chaosbuffalo.mkweapons.capabilities;

import com.chaosbuffalo.mkweapons.items.effects.ItemModifierEffect;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CurioAccessoryHandler extends AccessoryDataHandler implements ICurio {

    private final Map<String, Multimap<Attribute, AttributeModifier>> modifiers = new HashMap<>();

    public CurioAccessoryHandler(ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack) {
        forAllStackEffects(effect -> effect.onEntityEquip(slotContext.getWearer()));
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack) {
        forAllStackEffects(effect -> effect.onEntityUnequip(slotContext.getWearer()));
    }

    @Override
    public void onDynamicEffectsChanged() {
        super.onDynamicEffectsChanged();
        modifiers.clear();
    }

    private void loadSlotModifiers(String slotId) {
        Multimap<Attribute, AttributeModifier> newMods = HashMultimap.create();
        forAllStackEffects(effect -> {
            if (effect instanceof ItemModifierEffect) {
                ItemModifierEffect modEffect = (ItemModifierEffect) effect;
                modEffect.getModifiers().forEach(e -> newMods.put(e.getAttribute(), e.getModifier()));
            }
        });
        this.modifiers.put(slotId, newMods);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid) {
        if (!modifiers.containsKey(slotContext.getIdentifier())) {
            loadSlotModifiers(slotContext.getIdentifier());
        }
        return modifiers.get(slotContext.getIdentifier());
    }
}
