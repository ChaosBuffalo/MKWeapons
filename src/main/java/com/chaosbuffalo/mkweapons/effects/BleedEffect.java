package com.chaosbuffalo.mkweapons.effects;

import com.chaosbuffalo.mkcore.GameConstants;
import com.chaosbuffalo.mkcore.core.damage.MKDamageSource;
import com.chaosbuffalo.mkcore.effects.SpellCast;
import com.chaosbuffalo.mkcore.effects.SpellPeriodicEffectBase;
import com.chaosbuffalo.mkcore.fx.ParticleEffects;
import com.chaosbuffalo.mkcore.init.ModDamageTypes;
import com.chaosbuffalo.mkcore.network.PacketHandler;
import com.chaosbuffalo.mkcore.network.ParticleEffectSpawnPacket;
import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.init.MKWeaponsParticles;
import com.chaosbuffalo.targeting_api.TargetingContext;
import com.chaosbuffalo.targeting_api.TargetingContexts;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MKWeapons.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BleedEffect extends SpellPeriodicEffectBase {
    public static final String SCALING_CONTRIBUTION = "bleed.scaling_contribution";

    public static final BleedEffect INSTANCE = new BleedEffect();

    protected BleedEffect() {
        super(GameConstants.TICKS_PER_SECOND, EffectType.HARMFUL, 123);
        setRegistryName("effect.bleed_damage");
    }

    @Override
    protected boolean shouldShowParticles() {
        return false;
    }

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Effect> event) {
        event.getRegistry().register(INSTANCE);
    }

    @Override
    public TargetingContext getTargetContext() {
        return TargetingContexts.ALL;
    }

    public static SpellCast Create(Entity source, float baseDamage, float scaling) {
        return Create(source, baseDamage, scaling, 1.0f);
    }

    public static SpellCast Create(Entity source, float baseDamage, float scaling, float modifierScaling) {
        return INSTANCE.newSpellCast(source).setScalingParameters(baseDamage, scaling)
                .setFloat(SCALING_CONTRIBUTION, modifierScaling);
    }

    @Override
    public void doEffect(Entity applier, Entity caster, LivingEntity target, int i, SpellCast spellCast) {
        float damage = spellCast.getScaledValue(i);
        target.attackEntityFrom(MKDamageSource.causeEffectDamage(ModDamageTypes.BleedDamage, "bleed",
                applier, caster,  spellCast.getFloat(SCALING_CONTRIBUTION)), damage);
        PacketHandler.sendToTrackingMaybeSelf(
                new ParticleEffectSpawnPacket(
                        MKWeaponsParticles.DRIPPING_BLOOD,
                        ParticleEffects.DIRECTED_SPOUT, 8, 1,
                        target.getPosX(), target.getPosY() + target.getHeight() * .75,
                        target.getPosZ(), target.getWidth() / 2.0, 0.5, target.getWidth() / 2.0, 3,
                        target.getUpVector(0)), target);
    }
}
