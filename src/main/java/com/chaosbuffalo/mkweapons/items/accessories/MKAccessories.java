package com.chaosbuffalo.mkweapons.items.accessories;

import com.chaosbuffalo.mkweapons.capabilities.IAccessoryData;
import com.chaosbuffalo.mkweapons.capabilities.WeaponsCapabilities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class MKAccessories {

    private static final List<BiConsumer<LivingEntity, NonNullConsumer<IAccessoryData>>> specialInventoryAccessors = new ArrayList<>();

    public static void registerInventoryAccessor(BiConsumer<LivingEntity, NonNullConsumer<IAccessoryData>> lookup) {
        specialInventoryAccessors.add(lookup);
    }

    public static LazyOptional<IAccessoryData> getAccessoryHandler(ItemStack item) {
        return item.getCapability(WeaponsCapabilities.ACCESSORY_DATA_CAPABILITY);
    }

    public static void findAccessoryHandlers(LivingEntity entity, NonNullConsumer<IAccessoryData> consumer) {
        specialInventoryAccessors.forEach(c -> c.accept(entity, consumer));
    }
}
