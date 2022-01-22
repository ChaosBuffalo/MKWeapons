package com.chaosbuffalo.mkweapons.event;

import com.chaosbuffalo.mkcore.utils.ItemUtils;
import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.MKBow;
import com.chaosbuffalo.mkweapons.items.MKMeleeWeapon;
import com.chaosbuffalo.mkweapons.items.accessories.MKAccessory;
import com.chaosbuffalo.mkweapons.items.armor.MKArmorItem;
import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MKWeapons.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MKWeaponsClientEventHandler {

    @SubscribeEvent
    public static void onTooltipEvent(ItemTooltipEvent event){
        Item item = event.getItemStack().getItem();
        if (item instanceof MKMeleeWeapon){
            ((MKMeleeWeapon) item).addToTooltip(event.getItemStack(),
                    event.getPlayer() != null ? event.getPlayer().getEntityWorld() : null, event.getToolTip());
        }
        if (item instanceof MKBow){
            ((MKBow) item).addToTooltip(event.getItemStack(),
                    event.getPlayer() != null ? event.getPlayer().getEntityWorld() : null, event.getToolTip());
        }
        if (item instanceof MKArmorItem){
            ((MKArmorItem) item).addToTooltip(event.getItemStack(),
                    event.getPlayer() != null ? event.getPlayer().getEntityWorld() : null, event.getToolTip());
        }
        if (item instanceof MKAccessory){
            ((MKAccessory) item).addToTooltip(event.getItemStack(),
                    event.getPlayer() != null ? event.getPlayer().getEntityWorld() : null, event.getToolTip());
        }

        if (!(item instanceof IMKMeleeWeapon) && (item instanceof ToolItem || item instanceof SwordItem)){
            event.getToolTip().add(new StringTextComponent(I18n.format("mkweapons.crit_chance.description",
                    ItemUtils.getCritChanceForItem(event.getItemStack()) * 100.0f)).mergeStyle(TextFormatting.GRAY));
            event.getToolTip().add(new StringTextComponent(I18n.format("mkweapons.crit_multiplier.description",
                    ItemUtils.getCritMultiplierForItem(event.getItemStack()))).mergeStyle(TextFormatting.GRAY));
        }
    }
}
