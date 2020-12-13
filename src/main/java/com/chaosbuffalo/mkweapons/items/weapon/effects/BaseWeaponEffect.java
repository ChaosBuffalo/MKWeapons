package com.chaosbuffalo.mkweapons.items.weapon.effects;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public abstract class BaseWeaponEffect implements IWeaponEffect{

    private final ResourceLocation name;
    private final TextFormatting color;

    public BaseWeaponEffect(ResourceLocation name, TextFormatting color){
        this.name = name;
        this.color = color;
    }


    @Override
    public ResourceLocation getType() {
        return name;
    }


    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent(String.format("%s.%s.name",
                getType().getNamespace(), getType().getPath())).mergeStyle(color));
    }

    @Override
    public <D> void deserialize(Dynamic<D> dynamic) {

    }

    public static <D> ResourceLocation readType(Dynamic<D> dynamic){
        return new ResourceLocation(dynamic.get("type").asString("mkweapons:weapon_effect.error"));
    }


    @Override
    public <D> D serialize(DynamicOps<D> ops) {
        return ops.createMap(ImmutableMap.of(
                ops.createString("type"), ops.createString(getType().toString())
        ));
    }
}
