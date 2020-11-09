package com.chaosbuffalo.mkweapons.items.weapon.effects.ranged;

import com.chaosbuffalo.mkcore.MKCore;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class RapidFireRangedWeaponEffect implements IRangedWeaponEffect{
    private final int maxHits;
    private final float perHitReduction;

    public RapidFireRangedWeaponEffect(int maxHits, float perHitReduction){
        this.maxHits = maxHits;
        this.perHitReduction = perHitReduction;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn,
                               List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("mkweapons.effect.rapid_fire.name")
                .applyTextStyle(TextFormatting.DARK_RED));
        if (Screen.hasShiftDown()){
            tooltip.add(new StringTextComponent(I18n.format("mkweapons.effect.rapid_fire.description",
                    perHitReduction * 100.0f, maxHits * perHitReduction * 100.0f)));
        }
    }

    @Override
    public float modifyDrawTime(float inTime, ItemStack item, LivingEntity entity) {
        return MKCore.getEntityData(entity).map(cap -> {
            int totalToReduce = Math.min(cap.getCombatExtension().getCurrentProjectileHitCount(), maxHits);
            float timeReduction = totalToReduce * perHitReduction;
            return (1.0f - timeReduction) * inTime;
        }).orElse(inTime);

    }
}
