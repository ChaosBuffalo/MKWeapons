package com.chaosbuffalo.mkweapons.items.accessories;

import com.chaosbuffalo.mkweapons.capabilities.CapabilityProvider;
import com.chaosbuffalo.mkweapons.capabilities.CurioAccessoryHandler;
import com.chaosbuffalo.mkweapons.capabilities.WeaponsCapabilities;
import com.chaosbuffalo.mkweapons.items.effects.accesory.IAccessoryEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import top.theillusivec4.curios.api.CuriosCapability;

import javax.annotation.Nullable;

public class MKCurioAccessory extends MKAccessory {

    public MKCurioAccessory(Properties properties, IAccessoryEffect... effectsIn) {
        super(properties, effectsIn);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return initAccessoryCapability(CurioAccessoryHandler::new,
                cap -> CapabilityProvider.of(cap, WeaponsCapabilities.ACCESSORY_DATA_CAPABILITY, CuriosCapability.ITEM),
                stack, nbt);
    }
}
