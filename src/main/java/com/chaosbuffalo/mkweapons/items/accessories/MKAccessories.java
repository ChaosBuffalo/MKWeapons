package com.chaosbuffalo.mkweapons.items.accessories;

import com.chaosbuffalo.mkweapons.capabilities.MKCurioItemHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class MKAccessories {
    public static Optional<MKCurioItemHandler> getAccessoryHandler(ItemStack item) {
        Optional<ICurio> curioCap = item.getCapability(CuriosCapability.ITEM).resolve();
        if (curioCap.isPresent()) {
            ICurio cap = curioCap.get();
            if (cap instanceof MKCurioItemHandler) {
                return Optional.of((MKCurioItemHandler) cap);
            }
        }
        return Optional.empty();
    }

    public static List<MKCurioItemHandler> getMKCurios(LivingEntity entity) {
        List<MKCurioItemHandler> curios = new ArrayList<>();
        findAccessoryHandlers(entity, curios::add);
        return curios;
    }

    public static void findAccessoryHandlers(LivingEntity entity, Consumer<MKCurioItemHandler> consumer) {
        CuriosApi.getCuriosHelper().getEquippedCurios(entity).ifPresent(curioInventory -> {
            for (int i = 0; i < curioInventory.getSlots(); i++) {
                ItemStack curioIS = curioInventory.getStackInSlot(i);
                if (!curioIS.isEmpty() && curioIS.getItem() instanceof MKAccessory) {
                    getAccessoryHandler(curioIS).ifPresent(consumer);
                }
            }
        });
    }
}
