package com.chaosbuffalo.mkweapons.items.effects.accesory;

import com.chaosbuffalo.mkcore.abilities.MKAbility;
import com.chaosbuffalo.mkcore.core.IMKEntityData;
import com.chaosbuffalo.mkweapons.items.effects.IItemEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public interface IAccessoryEffect extends IItemEffect {


    default float modifyDamageDealt(LivingEntity attacker, LivingEntity target, ItemStack stack, float damage) {
        return damage;
    }

    default void livingCompleteAbility(IMKEntityData casterData, ItemStack stack, MKAbility ability) {

    }
}
