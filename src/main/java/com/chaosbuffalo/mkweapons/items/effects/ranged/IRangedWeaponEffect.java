package com.chaosbuffalo.mkweapons.items.effects.ranged;

import com.chaosbuffalo.mkcore.core.IMKEntityData;
import com.chaosbuffalo.mkweapons.items.effects.IItemEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public interface IRangedWeaponEffect extends IItemEffect {

    default void onProjectileHit(LivingHurtEvent event, DamageSource source, LivingEntity livingTarget,
                                 ServerPlayerEntity playerSource, IMKEntityData sourceData,
                                 AbstractArrowEntity arrow, ItemStack bow){

    }

    default float modifyDrawTime(float inTime, ItemStack item, LivingEntity entity){
        return inTime;
    }

    default float modifyLaunchVelocity(float inVel, ItemStack item, LivingEntity entity){
        return inVel;
    }
}
