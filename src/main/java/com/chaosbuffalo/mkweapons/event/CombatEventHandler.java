package com.chaosbuffalo.mkweapons.event;

import com.chaosbuffalo.mkcore.effects.SpellTriggers;
import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import com.chaosbuffalo.mkweapons.items.weapon.IWeaponEffectProvider;
import com.chaosbuffalo.mkweapons.items.weapon.effects.IWeaponEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MKWeapons.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CombatEventHandler {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity livingTarget = event.getEntityLiving();
        if (livingTarget.world.isRemote)
            return;
        DamageSource source = event.getSource();
        if (SpellTriggers.isMinecraftPhysicalDamage(source)){
            Entity trueSource = source.getTrueSource();
            if (trueSource instanceof LivingEntity){
                LivingEntity attacker = (LivingEntity)trueSource;
                ItemStack mainHand = attacker.getHeldItemMainhand();
                float newDamage = event.getAmount();
                if (!mainHand.isEmpty() && mainHand.getItem() instanceof IMKMeleeWeapon){
                    Item item = mainHand.getItem();
                    for (IWeaponEffect effect : ((IMKMeleeWeapon) item).getWeaponEffects()){
                        newDamage = effect.modifyDamageDealt(newDamage, (IMKMeleeWeapon) item,
                                mainHand, livingTarget, attacker);
                    }
                }
                event.setAmount(newDamage);
            }
        }
    }
}
