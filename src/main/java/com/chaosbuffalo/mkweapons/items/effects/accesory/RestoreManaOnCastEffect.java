package com.chaosbuffalo.mkweapons.items.effects.accesory;

import com.chaosbuffalo.mkcore.abilities.MKAbility;
import com.chaosbuffalo.mkcore.core.IMKEntityData;
import com.chaosbuffalo.mkcore.core.MKPlayerData;
import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.accessories.MKAccessory;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class RestoreManaOnCastEffect extends BaseAccessoryEffect {
    private double chance;
    private float percentage;
    public static final ResourceLocation NAME = new ResourceLocation(MKWeapons.MODID, "accessory_effect.restore_mana");

    public RestoreManaOnCastEffect() {
        super(NAME, TextFormatting.AQUA);
        chance = 0.0;
        percentage = 0.0f;
    }

    public RestoreManaOnCastEffect(double chance, float restorePercentage) {
        this();
        this.chance = chance;
        this.percentage = restorePercentage;
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public float getPercentage() {
        return percentage;
    }

    @Override
    public <D> void writeAdditionalData(DynamicOps<D> ops, ImmutableMap.Builder<D, D> builder) {
        super.writeAdditionalData(ops, builder);
        builder.put(ops.createString("chance"), ops.createDouble(chance));
        builder.put(ops.createString("percentage"), ops.createFloat(percentage));
    }

    @Override
    public <D> void readAdditionalData(Dynamic<D> dynamic) {
        chance = dynamic.get("chance").asDouble(0.0);
        percentage = dynamic.get("percentage").asFloat(0.0f);
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    @Override
    public void livingCompleteAbility(LivingEntity caster, IMKEntityData entityData, MKAccessory accessory,
                                      ItemStack stack, MKAbility ability) {
        if (!caster.getEntityWorld().isRemote() && entityData instanceof MKPlayerData) {
            MKPlayerData playerData = (MKPlayerData) entityData;
            double roll = caster.getRNG().nextDouble();
            if (roll >= (1.0 - getChance())) {
                float mana = ability.getManaCost(entityData) * getPercentage();
                playerData.getStats().addMana(mana);
                playerData.getEntity().sendMessage(new TranslationTextComponent(
                        "mkweapons.accessory_effect.restore_mana.message",
                        stack.getDisplayName()), Util.DUMMY_UUID);
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
        super.addInformation(stack, worldIn, tooltip);
        if (Screen.hasShiftDown()) {
            tooltip.add(new TranslationTextComponent("mkweapons.accessory_effect.restore_mana.description",
                    MKAbility.PERCENT_FORMATTER.format(getChance()), MKAbility.PERCENT_FORMATTER.format(getPercentage())));
        }
    }


}
