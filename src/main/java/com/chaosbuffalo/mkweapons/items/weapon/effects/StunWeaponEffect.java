package com.chaosbuffalo.mkweapons.items.weapon.effects;

import com.chaosbuffalo.mkcore.GameConstants;
import com.chaosbuffalo.mkcore.effects.SpellCast;
import com.chaosbuffalo.mkcore.effects.status.StunEffect;
import com.chaosbuffalo.mkcore.fx.ParticleEffects;
import com.chaosbuffalo.mkcore.network.PacketHandler;
import com.chaosbuffalo.mkcore.network.ParticleEffectSpawnPacket;
import com.chaosbuffalo.mkweapons.effects.BleedEffect;
import com.chaosbuffalo.mkweapons.init.MKWeaponsParticles;
import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class StunWeaponEffect implements IWeaponEffect {
    private final int stunDuration;
    private final double stunChance;

    public StunWeaponEffect(double stunChance, int stunSeconds){
        this.stunChance = stunChance;
        this.stunDuration = stunSeconds;
    }

    @Override
    public void onHit(IMKMeleeWeapon weapon, ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker.getRNG().nextDouble() > 1.0 - stunChance){
            SpellCast cast = com.chaosbuffalo.mkcore.effects.status.StunEffect.Create(attacker).setTarget(target);
            target.addPotionEffect(cast.toPotionEffect(stunDuration * GameConstants.TICKS_PER_SECOND,0));
            PacketHandler.sendToTrackingMaybeSelf(
                    new ParticleEffectSpawnPacket(
                            ParticleTypes.ENCHANTED_HIT,
                            ParticleEffects.CIRCLE_MOTION, 10, 1,
                            target.getPosX(), target.getPosY() + target.getEyeHeight(),
                            target.getPosZ(), target.getWidth() / 2.0, 0.5, target.getWidth() / 2.0, .25,
                            target.getUpVector(0)), target);

        }


    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("mkweapons.effect.stun.name")
                .applyTextStyle(TextFormatting.DARK_PURPLE));
        if (Screen.hasShiftDown()){
            tooltip.add(new StringTextComponent(I18n.format("mkweapons.effect.stun.description",
                    stunChance * 100.0, stunDuration)));
        }
    }
}
