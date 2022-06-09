package com.chaosbuffalo.mkweapons.items.randomization.options;

import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.randomization.slots.LootSlot;
import com.chaosbuffalo.mkweapons.items.randomization.slots.RandomizationSlotManager;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class PrefixNameOption extends BaseRandomizationOption{
    public static final ResourceLocation NAME = new ResourceLocation(MKWeapons.MODID, "prefix_name");

    private ITextComponent name;

    public PrefixNameOption(ITextComponent name) {
        super(NAME, RandomizationSlotManager.NAME_SLOT);
        this.name = name;
    }

    public PrefixNameOption(){
        this(new StringTextComponent(""));
    }


    @Override
    public void applyToItemStackForSlot(ItemStack stack, LootSlot slot, double difficulty) {

        stack.setDisplayName(new TranslationTextComponent("mkweapons.prefix.format", name, stack.getItem().getDisplayName(stack)));
    }

    @Override
    public <D> void writeAdditionalData(DynamicOps<D> ops, ImmutableMap.Builder<D, D> builder) {
        super.writeAdditionalData(ops, builder);
        builder.put(ops.createString("name"), ops.createString(ITextComponent.Serializer.toJson(name)));
    }

    @Override
    public <D> void readAdditionalData(Dynamic<D> dynamic) {
        super.readAdditionalData(dynamic);
        name = dynamic.get("name").map(x -> x.asString().result().map(ITextComponent.Serializer::getComponentFromJson)
                .orElse(new StringTextComponent("default"))).result().orElse(new StringTextComponent("default"));
    }
}
