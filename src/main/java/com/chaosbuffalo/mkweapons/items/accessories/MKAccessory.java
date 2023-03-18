package com.chaosbuffalo.mkweapons.items.accessories;

import com.chaosbuffalo.mkweapons.capabilities.AccessoryDataHandler;
import com.chaosbuffalo.mkweapons.capabilities.CapabilityProvider;
import com.chaosbuffalo.mkweapons.capabilities.WeaponsCapabilities;
import com.chaosbuffalo.mkweapons.items.effects.accesory.IAccessoryEffect;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class MKAccessory extends Item {

    private final List<IAccessoryEffect> staticEffects;

    public MKAccessory(Item.Properties properties, IAccessoryEffect... effectsIn) {
        super(properties);
        staticEffects = new ArrayList<>();
        staticEffects.addAll(Arrays.asList(effectsIn));
    }

    protected <T extends AccessoryDataHandler> ICapabilityProvider initAccessoryCapability(Function<ItemStack, T> capFactory,
                                                                                           Function<T, ICapabilityProvider> provFactory,
                                                                                           ItemStack stack,
                                                                                           @Nullable CompoundNBT nbt) {
        T handler = capFactory.apply(stack);
        ICapabilityProvider provider = provFactory.apply(handler);
        if (nbt != null) {
            handler.deserializeNBT(nbt);
        }
        return provider;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return initAccessoryCapability(AccessoryDataHandler::new,
                cap -> CapabilityProvider.of(cap, WeaponsCapabilities.ACCESSORY_DATA_CAPABILITY), stack, nbt);
    }

    public void addToTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
        MKAccessories.getAccessoryHandler(stack).ifPresent(c -> {
            c.forAllStackEffects(effect -> effect.addInformation(stack, worldIn, tooltip));
        });
    }

    @Nullable
    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        // See comment in MKMeleeWeapon#getShareTag
        CompoundNBT newTag = new CompoundNBT();
        CompoundNBT original = super.getShareTag(stack);
        if (original != null) {
            newTag.put("share", original);
        }
        MKAccessories.getAccessoryHandler(stack).ifPresent(cap -> newTag.put("accessoryCap", cap.serializeNBT()));
        return newTag;
    }

    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundNBT shareTag) {
        if (shareTag == null)
            return;

        if (shareTag.contains("share")) {
            super.readShareTag(stack, shareTag.getCompound("share"));
        }
        if (shareTag.contains("accessoryCap")) {
            MKAccessories.getAccessoryHandler(stack).ifPresent(cap -> {
                cap.deserializeNBT(shareTag.getCompound("accessoryCap"));
            });
        }
    }

    public void forEachStaticAccessoryEffect(Consumer<IAccessoryEffect> consumer) {
        staticEffects.forEach(consumer);
    }
}
