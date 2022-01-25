package com.chaosbuffalo.mkweapons.items.effects;

import com.chaosbuffalo.mkcore.serialization.IDynamicMapTypedSerializer;
import com.chaosbuffalo.mkweapons.MKWeapons;
import com.mojang.serialization.Dynamic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public abstract class BaseItemEffect implements IItemEffect {
    public final static ResourceLocation INVALID_EFFECT_TYPE = new ResourceLocation(MKWeapons.MODID, "weapon_effect.error");
    private final ResourceLocation name;
    protected final TextFormatting color;
    private static final String TYPE_ENTRY_NAME = "itemEffectType";

    public BaseItemEffect(ResourceLocation name, TextFormatting color){
        this.name = name;
        this.color = color;
    }

    @Override
    public ResourceLocation getTypeName() {
        return name;
    }

    @Override
    public String getTypeEntryName() {
        return TYPE_ENTRY_NAME;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
        tooltip.add(new TranslationTextComponent(String.format("%s.%s.name",
                this.getTypeName().getNamespace(), this.getTypeName().getPath())).mergeStyle(color));
    }


    public static <D> ResourceLocation getType(Dynamic<D> dynamic) {
        return IDynamicMapTypedSerializer.getType(dynamic, TYPE_ENTRY_NAME).orElse(INVALID_EFFECT_TYPE);
    }
}
