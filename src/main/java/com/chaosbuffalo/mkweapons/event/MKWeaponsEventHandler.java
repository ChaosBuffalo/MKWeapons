package com.chaosbuffalo.mkweapons.event;

import com.chaosbuffalo.mkcore.core.IMKEntityData;
import com.chaosbuffalo.mkcore.effects.SpellTriggers;
import com.chaosbuffalo.mkcore.events.EntityAbilityEvent;
import com.chaosbuffalo.mkcore.events.PostAttackEvent;
import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.capabilities.MKCurioItemHandler;
import com.chaosbuffalo.mkweapons.items.accessories.MKAccessory;
import com.chaosbuffalo.mkweapons.items.armor.IMKArmor;

import com.chaosbuffalo.mkweapons.items.effects.accesory.IAccessoryEffect;
import com.chaosbuffalo.mkweapons.items.weapon.IMKRangedWeapon;
import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import com.chaosbuffalo.mkweapons.items.weapon.IMKWeapon;
import com.chaosbuffalo.mkweapons.items.effects.melee.IMeleeWeaponEffect;
import com.chaosbuffalo.mkweapons.items.effects.ranged.IRangedWeaponEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = MKWeapons.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MKWeaponsEventHandler {

    private static void handleProjectileDamage(LivingHurtEvent event, DamageSource source, LivingEntity livingTarget,
                                               ServerPlayerEntity playerSource, IMKEntityData sourceData){
        if (source.getImmediateSource() instanceof AbstractArrowEntity){
            AbstractArrowEntity arrowEntity = (AbstractArrowEntity) source.getImmediateSource();
            MKWeapons.getArrowCapability(arrowEntity).ifPresent(cap -> {
                if (!livingTarget.isActiveItemStackBlocking()){
                    if (!cap.getShootingWeapon().isEmpty() && cap.getShootingWeapon().getItem() instanceof IMKRangedWeapon){
                        IMKRangedWeapon bow = (IMKRangedWeapon) cap.getShootingWeapon().getItem();
                        for (IRangedWeaponEffect effect : bow.getWeaponEffects(cap.getShootingWeapon())){
                            effect.onProjectileHit(event, source, livingTarget, playerSource, sourceData,
                                    arrowEntity, cap.getShootingWeapon());
                        }
                    }
                }
            });
        }
    }


    public static void registerCombatTriggers(){
        SpellTriggers.PLAYER_HURT_ENTITY.registerProjectile(MKWeaponsEventHandler::handleProjectileDamage);
    }

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        Item from = event.getFrom().getItem();
        Item to = event.getTo().getItem();
        if (from instanceof IMKWeapon){
            ((IMKWeapon) from).getWeaponEffects(event.getFrom()).forEach(
                    eff -> eff.onEntityUnequip(event.getEntityLiving())
            );
        }
        if (to instanceof IMKWeapon){
            ((IMKWeapon) to).getWeaponEffects(event.getTo()).forEach(
                    eff -> eff.onEntityEquip(event.getEntityLiving())
            );
        }
        if (from instanceof IMKArmor){
            ((IMKArmor) from).getArmorEffects(event.getFrom()).forEach(
                    eff -> eff.onEntityUnequip(event.getEntityLiving())
            );
        }
        if (to instanceof IMKArmor){
            ((IMKArmor) to).getArmorEffects(event.getTo()).forEach(
                    eff -> eff.onEntityEquip(event.getEntityLiving())
            );
        }
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
            for (IMeleeWeaponEffect effect : ((IMKMeleeWeapon) item).getWeaponEffects(mainHand)){
                effect.postAttack((IMKMeleeWeapon) item, mainHand, entity);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingCast(EntityAbilityEvent.EntityCompleteAbilityEvent event){
        List<MKCurioItemHandler> curios = MKAccessory.getMKCurios(event.getEntityLiving());
        for (MKCurioItemHandler handler : curios){
            for (IAccessoryEffect effect : handler.getEffects()){
                effect.livingCompleteAbility(event.getEntityLiving(), event.getEntityData(), handler.getAccessory(),
                        handler.getItemStack(), event.getAbility());
            }
        }
    }


    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity livingTarget = event.getEntityLiving();
        if (livingTarget.world.isRemote)
            return;
        DamageSource source = event.getSource();
        Entity trueSource = source.getTrueSource();
        float newDamage = event.getAmount();
        if (trueSource instanceof LivingEntity){
            LivingEntity livingSource = (LivingEntity)trueSource;
            if (SpellTriggers.isMinecraftPhysicalDamage(source)){
                ItemStack mainHand = livingSource.getHeldItemMainhand();
                if (!mainHand.isEmpty() && mainHand.getItem() instanceof IMKMeleeWeapon){
                    Item item = mainHand.getItem();
                    for (IMeleeWeaponEffect effect : ((IMKMeleeWeapon) item).getWeaponEffects(mainHand)){
                        newDamage = effect.modifyDamageDealt(newDamage, (IMKMeleeWeapon) item,
                                mainHand, livingTarget, livingSource);
                    }
                }
            }
            List<MKCurioItemHandler> curios = MKAccessory.getMKCurios(livingSource);
            for (MKCurioItemHandler handler : curios){
                for (IAccessoryEffect effect : handler.getEffects()){
                    newDamage = effect.modifyDamageDealt(newDamage, handler.getAccessory(),
                            handler.getItemStack(), livingTarget, livingSource);
                }
            }
        }
        event.setAmount(newDamage);
    }
}
