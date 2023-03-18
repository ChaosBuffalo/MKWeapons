package com.chaosbuffalo.mkweapons.event;

import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.IMKEquipment;
import com.chaosbuffalo.mkweapons.items.accessories.MKAccessory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MKWeapons.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MKWeaponsClientEventHandler {

    @SubscribeEvent
    public static void onTooltipEvent(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty())
            return;
        Item item = stack.getItem();

        if (item instanceof IMKEquipment) {
            ((IMKEquipment) item).addToTooltip(stack,
                    event.getPlayer() != null ? event.getPlayer().getEntityWorld() : null, event.getToolTip());
        }

        if (item instanceof MKAccessory) {
            ((MKAccessory) item).addToTooltip(stack,
                    event.getPlayer() != null ? event.getPlayer().getEntityWorld() : null, event.getToolTip());
        }
    }
}
