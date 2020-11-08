package com.chaosbuffalo.mkweapons.items.weapon.effects;

import com.chaosbuffalo.mkcore.MKCore;
import com.chaosbuffalo.mkcore.core.CombatExtensionModule;
import com.chaosbuffalo.mkcore.utils.EntityUtils;
import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
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

public class ComboStrikeWeaponEffect extends SwingWeaponEffect {

    public ComboStrikeWeaponEffect(int numberOfHits, double perHit) {
        super(numberOfHits, perHit);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("mkweapons.effect.combo_strike.name")
                .applyTextStyle(TextFormatting.GREEN));
        if (Screen.hasShiftDown()){
            tooltip.add(new StringTextComponent(I18n.format("mkweapons.effect.combo_strike.description",
                    getPerHit() * 100.0f, getNumberOfHits())));
        }
    }

    @Override
    public void postAttack(IMKMeleeWeapon weapon, ItemStack stack, LivingEntity attacker) {
        MKCore.getEntityData(attacker).ifPresent(cap -> {
            CombatExtensionModule combatModule = cap.getCombatExtension();
            if (combatModule.isMidCombo()) {
                int hit = combatModule.getCurrentSwingCount() % getNumberOfHits();
                double totalReduction = hit * getPerHit();
                double cooldownPeriod = EntityUtils.getCooldownPeriod(attacker);
                int newTicks = (int) Math.round(cooldownPeriod * totalReduction);
                combatModule.setTicksSinceSwing(newTicks);
            }
        });
    }
}
