package com.chaosbuffalo.mkweapons.items.effects;

import com.chaosbuffalo.mkcore.serialization.IDynamicMapTypedSerializer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public interface IItemEffect extends IDynamicMapTypedSerializer {

    void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip);

    default void onEntityEquip(LivingEntity entity) {}

    default void onEntityUnequip(LivingEntity entity) {}
}
