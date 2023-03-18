package com.chaosbuffalo.mkweapons.items.armor;

import com.chaosbuffalo.mkweapons.capabilities.ArmorDataHandler;
import com.chaosbuffalo.mkweapons.capabilities.CapabilityProvider;
import com.chaosbuffalo.mkweapons.capabilities.IArmorData;
import com.chaosbuffalo.mkweapons.capabilities.WeaponsCapabilities;
import com.chaosbuffalo.mkweapons.items.IMKEquipment;
import com.chaosbuffalo.mkweapons.items.effects.ItemModifierEffect;
import com.chaosbuffalo.mkweapons.items.effects.armor.IArmorEffect;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class MKArmorItem extends ArmorItem implements IMKEquipment {
    public static final UUID CHEST_UUID = UUID.fromString("77ab4b54-5885-4f7f-ab41-71af536309d1");
    public static final UUID LEGGINGS_UUID = UUID.fromString("8d827c58-8f61-4c77-8dcd-f62f0e69121b");
    public static final UUID HELMET_UUID = UUID.fromString("fb16408c-0421-4138-a283-8da7038e5970");
    public static final UUID FEET_UUID = UUID.fromString("fb16408c-0421-4138-a283-8da7038e5972");
    public static final UUID[] ARMOR_MODIFIERS = new UUID[]{FEET_UUID, LEGGINGS_UUID, CHEST_UUID, HELMET_UUID};
    private final List<IArmorEffect> armorEffects;

    private final Multimap<Attribute, AttributeModifier> attributeMap;


    public MKArmorItem(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builderIn,
                       IArmorEffect... armorEffects) {
        super(materialIn, slot, builderIn);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        UUID uuid = ARMOR_MODIFIERS[slot.getIndex()];
        builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", getDamageReduceAmount(),
                AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", getToughness(),
                AttributeModifier.Operation.ADDITION));
        if (this.knockbackResistance > 0) {
            builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "Armor knockback resistance",
                    knockbackResistance, AttributeModifier.Operation.ADDITION));
        }
        buildAttributes(builder, slot, materialIn, uuid);
        this.attributeMap = builder.build();
        this.armorEffects = Arrays.asList(armorEffects);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        IArmorData data = new ArmorDataHandler(stack);
        return CapabilityProvider.of(data, WeaponsCapabilities.ARMOR_DATA_CAPABILITY);
    }

    protected void buildAttributes(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder,
                                   EquipmentSlotType slot, IArmorMaterial material, UUID slotUUID) {

    }

    @Nullable
    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        // See comment in MKMeleeWeapon#getShareTag
        CompoundNBT newTag = new CompoundNBT();
        CompoundNBT original = super.getShareTag(stack);
        if (original != null) {
            newTag.put("share", original);
        }
        stack.getCapability(WeaponsCapabilities.ARMOR_DATA_CAPABILITY).ifPresent(armorData ->
                newTag.put("armorCap", armorData.serializeNBT()));
        return newTag;
    }

    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundNBT shareTag) {
        if (shareTag == null)
            return;

        if (shareTag.contains("share")) {
            super.readShareTag(stack, shareTag.getCompound("share"));
        }
        if (shareTag.contains("armorCap")) {
            stack.getCapability(WeaponsCapabilities.ARMOR_DATA_CAPABILITY).ifPresent(armorData ->
                    armorData.deserializeNBT(shareTag.getCompound("armorCap")));
        }
    }

    @Override
    public void addToTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
        forEachEffect(stack, armorEffect -> armorEffect.addInformation(stack, worldIn, tooltip));
    }

    @Override
    public void onEquipped(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlotType slotType) {
        forEachEffect(itemStack, armorEffect -> armorEffect.onEntityEquip(livingEntity));
    }

    @Override
    public void onUnequipped(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlotType slotType) {
        forEachEffect(itemStack, armorEffect -> armorEffect.onEntityUnequip(livingEntity));
    }

    // Common + stack-specific effects
    public void forEachEffect(ItemStack stack, Consumer<IArmorEffect> consumer) {
        forEachEffect(consumer);
        stack.getCapability(WeaponsCapabilities.ARMOR_DATA_CAPABILITY).ifPresent(cap -> cap.forEachEffect(consumer));
    }

    // Only common effects
    public void forEachEffect(Consumer<IArmorEffect> consumer) {
        armorEffects.forEach(consumer);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        if (slot == getEquipmentSlot()) {
            Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create();
            modifiers.putAll(getAttributeModifiers(slot));

            // Iterate common+stack effects
            forEachEffect(stack, armorEffect -> {
                if (armorEffect instanceof ItemModifierEffect) {
                    ItemModifierEffect modEffect = (ItemModifierEffect) armorEffect;
                    modEffect.getModifiers().forEach(e -> modifiers.put(e.getAttribute(), e.getModifier()));
                }
            });
            return modifiers;
        } else {
            return getAttributeModifiers(slot);
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot) {
        return equipmentSlot == this.slot ? this.attributeMap : super.getAttributeModifiers(equipmentSlot);
    }
}
