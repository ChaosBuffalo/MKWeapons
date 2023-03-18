package com.chaosbuffalo.mkweapons.event;

import com.chaosbuffalo.mkcore.core.IMKEntityData;
import com.chaosbuffalo.mkcore.effects.SpellTriggers;
import com.chaosbuffalo.mkcore.events.EntityAbilityEvent;
import com.chaosbuffalo.mkcore.events.PostAttackEvent;
import com.chaosbuffalo.mkcore.utils.DamageUtils;
import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.capabilities.IArrowData;
import com.chaosbuffalo.mkweapons.items.IMKEquipment;
import com.chaosbuffalo.mkweapons.items.accessories.MKAccessories;
import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import com.chaosbuffalo.mkweapons.items.weapon.IMKRangedWeapon;
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

@Mod.EventBusSubscriber(modid = MKWeapons.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MKWeaponsEventHandler {

    private static void handleProjectileDamage(LivingHurtEvent event, DamageSource source, LivingEntity livingTarget,
                                               LivingEntity livingSource, IMKEntityData sourceData) {
        if (source.getImmediateSource() instanceof AbstractArrowEntity) {
            AbstractArrowEntity arrowEntity = (AbstractArrowEntity) source.getImmediateSource();
            if (!livingTarget.isActiveItemStackBlocking()) {
                IArrowData.get(arrowEntity).ifPresent(cap -> {
                    if (cap.getShootingWeapon().getItem() instanceof IMKRangedWeapon) {
                        IMKRangedWeapon bow = (IMKRangedWeapon) cap.getShootingWeapon().getItem();
                        bow.forEachRangedEffect(cap.getShootingWeapon(), effect -> {
                            effect.onProjectileHit(event, source, livingTarget, sourceData,
                                    arrowEntity, cap.getShootingWeapon());
                        });
                    }
                });
            }
        }
    }


    public static void registerCombatTriggers() {
        SpellTriggers.LIVING_HURT_ENTITY.registerProjectile(MKWeaponsEventHandler::handleProjectileDamage);
    }

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!event.getFrom().isEmpty()) {
            Item from = event.getFrom().getItem();
            if (from instanceof IMKEquipment) {
                ((IMKEquipment) from).onUnequipped(event.getEntityLiving(), event.getFrom(), event.getSlot());
            }
        }
        if (!event.getTo().isEmpty()) {
            Item to = event.getTo().getItem();
            if (to instanceof IMKEquipment) {
                ((IMKEquipment) to).onEquipped(event.getEntityLiving(), event.getTo(), event.getSlot());
            }
        }

        if (event.getEntityLiving() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getEntityLiving();
            checkShieldRestriction(player);
        }
    }

    private static void checkShieldRestriction(ServerPlayerEntity player) {
        ItemStack main = player.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
        ItemStack offhand = player.getItemStackFromSlot(EquipmentSlotType.OFFHAND);
        if (!main.isEmpty() && !offhand.isEmpty() && main.getItem() instanceof IMKMeleeWeapon) {
            IMKMeleeWeapon weapon = (IMKMeleeWeapon) main.getItem();
            if (weapon.getWeaponType().isTwoHanded() && offhand.getItem() instanceof ShieldItem) {
                ItemStack off = player.getItemStackFromSlot(EquipmentSlotType.OFFHAND);
                if (!player.inventory.addItemStackToInventory(off)) {
                    player.dropItem(off, true);
                }
                player.setItemStackToSlot(EquipmentSlotType.OFFHAND, ItemStack.EMPTY);
            }
        }
    }

    @SubscribeEvent
    public static void onPostCombatEvent(PostAttackEvent event) {
        LivingEntity entity = event.getEntityLiving();
        ItemStack mainHand = entity.getHeldItemMainhand();
        if (!mainHand.isEmpty() && mainHand.getItem() instanceof IMKMeleeWeapon) {
            IMKMeleeWeapon item = (IMKMeleeWeapon) mainHand.getItem();
            item.forEachMeleeEffect(mainHand, effect -> effect.postAttack(item, mainHand, entity));
        }
    }

    @SubscribeEvent
    public static void onLivingCast(EntityAbilityEvent.EntityCompleteAbilityEvent event) {
        MKAccessories.findAccessoryHandlers(event.getEntityLiving(), handler -> {
            handler.forAllStackEffects(effect -> {
                effect.livingCompleteAbility(event.getEntityData(), handler.getItemStack(), event.getAbility());
            });
        });
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity livingTarget = event.getEntityLiving();
        if (livingTarget.world.isRemote)
            return;
        DamageSource source = event.getSource();
        Entity trueSource = source.getTrueSource();
        if (trueSource instanceof LivingEntity) {
            LivingEntity livingSource = (LivingEntity) trueSource;
            if (DamageUtils.isMinecraftPhysicalDamage(source)) {
                ItemStack mainHand = livingSource.getHeldItemMainhand();
                if (!mainHand.isEmpty() && mainHand.getItem() instanceof IMKMeleeWeapon) {
                    IMKMeleeWeapon item = (IMKMeleeWeapon) mainHand.getItem();
                    item.forEachMeleeEffect(mainHand, effect -> {
                        float orig = event.getAmount();
                        float damage = effect.modifyDamageDealt(orig, item, mainHand, livingTarget, livingSource);
                        event.setAmount(damage);
                    });
                }
            }

            MKAccessories.findAccessoryHandlers(livingSource, handler -> {
                handler.forAllStackEffects(effect -> {
                    float orig = event.getAmount();
                    float dmg = effect.modifyDamageDealt(livingSource, livingTarget, handler.getItemStack(), orig);
                    event.setAmount(dmg);
                });
            });
        }
    }
}
