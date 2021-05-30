package com.chaosbuffalo.mkweapons.event;

import com.chaosbuffalo.mkcore.utils.ItemUtils;
import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.MKBow;
import com.chaosbuffalo.mkweapons.items.MKMeleeWeapon;
import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = MKWeapons.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MKWeaponsClientEventHandler {

    @SubscribeEvent
    public static void onTooltipEvent(ItemTooltipEvent event){
        if (event.getItemStack().getItem() instanceof MKMeleeWeapon){
            ((MKMeleeWeapon) event.getItemStack().getItem()).addToTooltip(event.getItemStack(),
                    event.getPlayer().getEntityWorld(), event.getToolTip());
        }
        if (event.getItemStack().getItem() instanceof MKBow){
            ((MKBow) event.getItemStack().getItem()).addToTooltip(event.getItemStack(),
                    event.getPlayer().getEntityWorld(), event.getToolTip());
        }
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
                    ItemUtils.getCritChanceForItem(event.getItemStack()) * 100.0f)).mergeStyle(TextFormatting.GRAY));
            event.getToolTip().add(new StringTextComponent(I18n.format("mkweapons.crit_multiplier.description",
                    ItemUtils.getCritMultiplierForItem(event.getItemStack()))).mergeStyle(TextFormatting.GRAY));
        }
    }
}
