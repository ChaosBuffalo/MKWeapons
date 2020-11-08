package com.chaosbuffalo.mkweapons.event;

import com.chaosbuffalo.mkcore.effects.SpellTriggers;
import com.chaosbuffalo.mkcore.events.PostAttackEvent;
import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import com.chaosbuffalo.mkweapons.items.weapon.effects.IWeaponEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MKWeapons.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MKWeaponsEventHandler {

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!(event.getEntityLiving() instanceof ServerPlayerEntity))
            return;
        ServerPlayerEntity player = (ServerPlayerEntity) event.getEntityLiving();
        checkShieldRestriction(player);
    }

    private static void checkShieldRestriction(ServerPlayerEntity player) {
        ItemStack main = player.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
        ItemStack offhand = player.getItemStackFromSlot(EquipmentSlotType.OFFHAND);
        if (main.getItem() instanceof IMKMeleeWeapon){
            IMKMeleeWeapon weapon = (IMKMeleeWeapon) main.getItem();
            if (weapon.getWeaponType().isTwoHanded() && offhand.getItem() instanceof ShieldItem){
                ItemStack off = player.getItemStackFromSlot(EquipmentSlotType.OFFHAND);
                if (!player.inventory.addItemStackToInventory(off)) {
                    player.dropItem(off, true);
                }
                player.setItemStackToSlot(EquipmentSlotType.OFFHAND, ItemStack.EMPTY);
            }
        }
    }

    @SubscribeEvent
    public static void onPostCombatEvent(PostAttackEvent event){
        LivingEntity entity = event.getEntityLiving();
        ItemStack mainHand = entity.getHeldItemMainhand();
        if (!mainHand.isEmpty() && mainHand.getItem() instanceof IMKMeleeWeapon){
            Item item = mainHand.getItem();
            for (IWeaponEffect effect : ((IMKMeleeWeapon) item).getWeaponEffects()){
                effect.postAttack((IMKMeleeWeapon) item, mainHand, entity);
            }
        }
    }

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
