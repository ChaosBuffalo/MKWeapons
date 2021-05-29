package com.chaosbuffalo.mkweapons.items.weapon.effects;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public interface IWeaponEffect {

    ResourceLocation getType();

    void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn);

    <D> D serialize(DynamicOps<D> ops);

    <D> void deserialize(Dynamic<D> dynamic);

    default void onEntityEquip(LivingEntity entity) {}

    default void onEntityUnequip(LivingEntity entity) {}
}
