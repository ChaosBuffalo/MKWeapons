package com.chaosbuffalo.mkweapons.items.effects.melee;

import com.chaosbuffalo.mkcore.GameConstants;
import com.chaosbuffalo.mkcore.MKCore;
import com.chaosbuffalo.mkcore.effects.MKEffectBuilder;
import com.chaosbuffalo.mkcore.effects.status.StunEffect;
import com.chaosbuffalo.mkcore.network.MKParticleEffectSpawnPacket;
import com.chaosbuffalo.mkcore.network.PacketHandler;
import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
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
    public static final ResourceLocation PARTICLES = new ResourceLocation(MKWeapons.MODID, "stun_effect");

    public StunMeleeWeaponEffect(double stunChance, int stunSeconds) {
        this();
        this.stunChance = stunChance;
        this.stunDuration = stunSeconds;
    }

    public StunMeleeWeaponEffect() {
        super(NAME, TextFormatting.DARK_PURPLE);
    }

    @Override
    public <D> void readAdditionalData(Dynamic<D> dynamic) {
        super.readAdditionalData(dynamic);
        stunChance = dynamic.get("chance").asDouble(0.05);
        stunDuration = dynamic.get("duration").asInt(2);
    }

    @Override
    public <D> void writeAdditionalData(DynamicOps<D> ops, ImmutableMap.Builder<D, D> builder) {
        super.writeAdditionalData(ops, builder);
        builder.put(ops.createString("duration"), ops.createInt(stunDuration));
        builder.put(ops.createString("chance"), ops.createDouble(stunChance));
    }

    @Override
    public void onHit(IMKMeleeWeapon weapon, ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker.getRNG().nextDouble() >= (1.0 - stunChance)) {
            MKEffectBuilder<?> stun = StunEffect.from(attacker)
                    .timed(stunDuration * GameConstants.TICKS_PER_SECOND);
            MKCore.getEntityData(target).ifPresent(targetData -> targetData.getEffects().addEffect(stun));
            PacketHandler.sendToTrackingAndSelf(new MKParticleEffectSpawnPacket(
                    new Vector3d(0.0, target.getHeight(), 0.0), PARTICLES, target.getEntityId()), target);
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
        super.addInformation(stack, worldIn, tooltip);
        if (Screen.hasShiftDown()) {
            tooltip.add(new StringTextComponent(I18n.format("mkweapons.weapon_effect.stun.description",
                    stunChance * 100.0, stunDuration)));
        }
    }
}
