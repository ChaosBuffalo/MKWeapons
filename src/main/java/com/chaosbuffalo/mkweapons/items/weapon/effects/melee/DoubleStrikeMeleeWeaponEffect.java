package com.chaosbuffalo.mkweapons.items.weapon.effects.melee;

import com.chaosbuffalo.mkcore.MKCore;
import com.chaosbuffalo.mkcore.core.CombatExtensionModule;
import com.chaosbuffalo.mkcore.network.PacketHandler;
import com.chaosbuffalo.mkcore.network.ResetAttackSwingPacket;
import com.chaosbuffalo.mkcore.utils.EntityUtils;
import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class DoubleStrikeMeleeWeaponEffect implements IMeleeWeaponEffect {

    private final double chance;

    public DoubleStrikeMeleeWeaponEffect(double chance){
        this.chance = chance;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("mkweapons.effect.double_strike.name")
                .applyTextStyle(TextFormatting.DARK_AQUA));
        if (Screen.hasShiftDown()){
            tooltip.add(new StringTextComponent(I18n.format("mkweapons.effect.double_strike.description",
                    chance * 100.0f)));
        }
    }

    @Override
    public void postAttack(IMKMeleeWeapon weapon, ItemStack stack, LivingEntity attacker) {
        if (attacker.getEntityWorld().isRemote()){
            return;
        }
        MKCore.getEntityData(attacker).ifPresent(cap -> {
            if (attacker.getRNG().nextDouble() >= 1.0 - chance){
                CombatExtensionModule combatModule = cap.getCombatExtension();
                double cooldownPeriod = EntityUtils.getCooldownPeriod(attacker);
                combatModule.setTicksSinceSwing((int) cooldownPeriod);
                if (attacker instanceof ServerPlayerEntity){
                    PacketHandler.sendMessage(new ResetAttackSwingPacket((int) cooldownPeriod),
                            (ServerPlayerEntity) attacker);
                }
            }
        });
    }
}
