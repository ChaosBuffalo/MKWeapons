package com.chaosbuffalo.mkweapons.event;

import com.chaosbuffalo.mkcore.core.MKAttributes;
import com.chaosbuffalo.mkcore.core.stats.CriticalStats;
import com.chaosbuffalo.mkcore.effects.SpellTriggers;
import com.chaosbuffalo.mkcore.events.PostAttackEvent;
import com.chaosbuffalo.mkcore.utils.ItemUtils;
import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.MKMeleeWeapon;
import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import com.chaosbuffalo.mkweapons.items.weapon.effects.IWeaponEffect;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = MKWeapons.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MKWeaponsEventHandler {
    private static final UUID CRIT_CHANCE_MODIFIER = UUID.fromString("3935094f-87c5-49a8-bcde-ea29ce3bb5f9");
    private static final UUID CRIT_MULT_MODIFIER = UUID.fromString("c167f8f7-7bfc-4232-a321-ba635a4eb46f");

    private static AttributeModifier createSlotModifier(UUID uuid, double amount, RangedAttribute mod) {
        return new AttributeModifier(uuid, mod::getName, amount, AttributeModifier.Operation.ADDITION).setSaved(false);
    }

    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!(event.getEntityLiving() instanceof ServerPlayerEntity))
            return;
        ServerPlayerEntity player = (ServerPlayerEntity) event.getEntityLiving();
        checkShieldRestriction(player);
        if (event.getSlot() == EquipmentSlotType.MAINHAND){
            Item from = event.getFrom().getItem();
            if (!(from instanceof IMKMeleeWeapon) && (from instanceof ToolItem || from instanceof SwordItem || from instanceof HoeItem) ){
                player.getAttribute(MKAttributes.MELEE_CRIT).removeModifier(CRIT_CHANCE_MODIFIER);
                player.getAttribute(MKAttributes.MELEE_CRIT_MULTIPLIER).removeModifier(CRIT_MULT_MODIFIER);
            }
            Item to = event.getTo().getItem();
            if (!(to instanceof IMKMeleeWeapon) && (to instanceof ToolItem || to instanceof SwordItem || to instanceof HoeItem)){
                player.getAttribute(MKAttributes.MELEE_CRIT).applyModifier(createSlotModifier(
                        CRIT_CHANCE_MODIFIER, ItemUtils.getCritChanceForItem(event.getTo()),
                        MKAttributes.MELEE_CRIT));
                player.getAttribute(MKAttributes.MELEE_CRIT_MULTIPLIER).applyModifier(createSlotModifier(
                        CRIT_MULT_MODIFIER, ItemUtils.getCritMultiplierForItem(event.getTo()),
                        MKAttributes.MELEE_CRIT_MULTIPLIER));
            }

        }
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
    public static void onToolTipEvent(ItemTooltipEvent event){
        Item item = event.getItemStack().getItem();
        if (item instanceof IMKMeleeWeapon){
            List<ITextComponent> toRemove = new ArrayList<>();
            for (ITextComponent component : event.getToolTip()){
                if (component.getString().contains("Melee Crit")){
                    toRemove.add(component);
                }
            }
            for (ITextComponent component : toRemove){
                event.getToolTip().remove(component);
            }
        } else if (item instanceof ToolItem || item instanceof SwordItem || item instanceof HoeItem){
            event.getToolTip().add(new StringTextComponent(I18n.format("mkweapons.crit_chance.description",
                    ItemUtils.getCritChanceForItem(event.getItemStack()) * 100.0f)).applyTextStyle(TextFormatting.GRAY));
            event.getToolTip().add(new StringTextComponent(I18n.format("mkweapons.crit_multiplier.description",
                    ItemUtils.getCritMultiplierForItem(event.getItemStack()))).applyTextStyle(TextFormatting.GRAY));
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
