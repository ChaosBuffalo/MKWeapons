package com.chaosbuffalo.mkweapons.items.weapon.effects.melee;

import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import com.chaosbuffalo.mkweapons.items.weapon.effects.IWeaponEffect;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public interface IMeleeWeaponEffect extends IWeaponEffect {

    default void onHit(IMKMeleeWeapon weapon, ItemStack stack,
                       LivingEntity target, LivingEntity attacker) { }

    default float modifyDamageDealt(float damage, IMKMeleeWeapon weapon, ItemStack stack,
                                    LivingEntity target, LivingEntity attacker){
        return damage;
    }

    default void postAttack(IMKMeleeWeapon weapon, ItemStack stack, LivingEntity attacker){

    }


}
