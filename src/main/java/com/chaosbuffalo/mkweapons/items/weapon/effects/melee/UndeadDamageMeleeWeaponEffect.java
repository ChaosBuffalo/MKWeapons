package com.chaosbuffalo.mkweapons.items.weapon.effects.melee;

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

public class UndeadDamageMeleeWeaponEffect implements IMeleeWeaponEffect {
    private final float damageMultiplier;
    public UndeadDamageMeleeWeaponEffect(float multiplier){
        this.damageMultiplier = multiplier;
    }

    @Override
    public float modifyDamageDealt(float damage, IMKMeleeWeapon weapon, ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target.isEntityUndead()){
            return damage * damageMultiplier;
        } else {
            return damage;
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("mkweapons.effect.undead_damage.name").applyTextStyle(TextFormatting.GOLD));
        if (Screen.hasShiftDown()){
            tooltip.add(new StringTextComponent(I18n.format("mkweapons.effect.undead_damage.description",
                    damageMultiplier)));
        }
    }
}
