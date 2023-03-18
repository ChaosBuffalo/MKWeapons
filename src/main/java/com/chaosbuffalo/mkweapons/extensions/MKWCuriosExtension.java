package com.chaosbuffalo.mkweapons.extensions;

import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.capabilities.IAccessoryData;
import com.chaosbuffalo.mkweapons.items.accessories.MKAccessories;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.fml.InterModComms;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

public class MKWCuriosExtension {

    public static void install() {
        sendExtension();
        MKAccessories.registerInventoryAccessor(MKWCuriosExtension::findCurioAccessoryHandlers);
    }

    public static void findCurioAccessoryHandlers(LivingEntity entity, NonNullConsumer<IAccessoryData> consumer) {
        CuriosApi.getCuriosHelper().getEquippedCurios(entity).ifPresent(curioInventory -> {
            for (int i = 0; i < curioInventory.getSlots(); i++) {
                ItemStack curioIS = curioInventory.getStackInSlot(i);
                if (!curioIS.isEmpty()) {
                    MKAccessories.getAccessoryHandler(curioIS).ifPresent(consumer);
                }
            }
        });
    }

    public static void sendExtension() {
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE,
                () -> SlotTypePreset.RING.getMessageBuilder().size(2).build());
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE,
                () -> new SlotTypeMessage.Builder("earring").priority(220).icon(
                        new ResourceLocation(MKWeapons.MODID, "item/empty_earring_slot")).size(2).build());
    }
}
