package com.chaosbuffalo.mkweapons.items.weapon.effects.melee;

import com.chaosbuffalo.mkcore.GameConstants;
import com.chaosbuffalo.mkcore.effects.SpellCast;
import com.chaosbuffalo.mkcore.fx.ParticleEffects;
import com.chaosbuffalo.mkcore.network.PacketHandler;
import com.chaosbuffalo.mkcore.network.ParticleEffectSpawnPacket;
import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class StunMeleeWeaponEffect extends BaseMeleeWeaponEffect {
    private int stunDuration;
    private double stunChance;
    public static final ResourceLocation NAME = new ResourceLocation(MKWeapons.MODID, "weapon_effect.stun");

    public StunMeleeWeaponEffect(double stunChance, int stunSeconds){
        this();
        this.stunChance = stunChance;
        this.stunDuration = stunSeconds;
    }

    public StunMeleeWeaponEffect(){
        super(NAME, TextFormatting.DARK_PURPLE);
    }

    @Override
    public <D> void deserialize(Dynamic<D> dynamic) {
        super.deserialize(dynamic);
        stunChance = dynamic.get("chance").asDouble(0.05);
        stunDuration = dynamic.get("duration").asInt(2);
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops) {
        return ops.mergeToMap(super.serialize(ops), ImmutableMap.of(
                ops.createString("duration"), ops.createInt(stunDuration),
                ops.createString("chance"), ops.createDouble(stunChance)
        )).result().orElse(ops.createMap(ImmutableMap.of()));
    }

    @Override
    public void onHit(IMKMeleeWeapon weapon, ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker.getRNG().nextDouble() > 1.0 - stunChance){
            SpellCast cast = com.chaosbuffalo.mkcore.effects.status.StunEffect.Create(attacker).setTarget(target);
            target.addPotionEffect(cast.toPotionEffect(stunDuration * GameConstants.TICKS_PER_SECOND,0));
            PacketHandler.sendToTrackingAndSelf(
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
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (Screen.hasShiftDown()){
            tooltip.add(new StringTextComponent(I18n.format("mkweapons.weapon_effect.stun.description",
                    stunChance * 100.0, stunDuration)));
        }
    }
}
