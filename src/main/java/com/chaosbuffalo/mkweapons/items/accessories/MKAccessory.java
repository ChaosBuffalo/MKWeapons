package com.chaosbuffalo.mkweapons.items.accessories;

import com.chaosbuffalo.mkweapons.capabilities.CapabilityProvider;
import com.chaosbuffalo.mkweapons.capabilities.MKCurioItemHandler;
import com.chaosbuffalo.mkweapons.items.effects.accesory.IAccessoryEffect;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import top.theillusivec4.curios.api.CuriosCapability;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MKAccessory extends Item {

    private final List<IAccessoryEffect> effects;

    public MKAccessory(Properties properties, IAccessoryEffect... effectsIn) {
        super(properties);
        effects = new ArrayList<>();
        effects.addAll(Arrays.asList(effectsIn));
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        MKCurioItemHandler handler = new MKCurioItemHandler(stack);
        ICapabilityProvider provider = CapabilityProvider.of(handler, CuriosCapability.ITEM);
        if (nbt != null) {
            handler.deserializeNBT(nbt);
        }
        return provider;
    }

    public List<? extends IAccessoryEffect> getAccessoryEffects(ItemStack item) {
        return item.getCapability(CuriosCapability.ITEM).map(cap -> {
            if (cap instanceof MKCurioItemHandler) {
                return ((MKCurioItemHandler) cap).getEffects();
            } else {
                return effects;
            }
        }).orElse(effects);
    }

    public List<IAccessoryEffect> getAccessoryEffects() {
        return effects;
    }

    public void addToTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
        for (IAccessoryEffect accessoryEffect : getAccessoryEffects(stack)) {
            accessoryEffect.addInformation(stack, worldIn, tooltip);
        }
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
}
