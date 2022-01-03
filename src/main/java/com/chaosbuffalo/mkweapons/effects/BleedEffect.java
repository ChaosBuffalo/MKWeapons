package com.chaosbuffalo.mkweapons.effects;

import com.chaosbuffalo.mkcore.core.IMKEntityData;
import com.chaosbuffalo.mkcore.core.damage.MKDamageSource;
import com.chaosbuffalo.mkcore.effects.*;
import com.chaosbuffalo.mkcore.fx.ParticleEffects;
import com.chaosbuffalo.mkcore.init.CoreDamageTypes;
import com.chaosbuffalo.mkcore.network.PacketHandler;
import com.chaosbuffalo.mkcore.network.ParticleEffectSpawnPacket;
import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.init.MKWeaponsParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = MKWeapons.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BleedEffect extends MKEffect {

    public static final BleedEffect INSTANCE = new BleedEffect();

    @SubscribeEvent
    public static void register(RegistryEvent.Register<MKEffect> event) {
        event.getRegistry().register(INSTANCE);
    }

    private BleedEffect() {
        super(EffectType.HARMFUL);
        setRegistryName("effect.bleed_damage");
    }

    @Override
    public MKEffectBuilder<State> builder(UUID sourceId) {
        return new MKEffectBuilder<>(this, sourceId, this::makeState);
    }

    @Override
    public State makeState() {
        return new State();
    }

    public static class State extends ScalingValueEffectState {
        private Entity source;

        @Override
        public boolean performEffect(IMKEntityData targetData, MKActiveEffect activeEffect) {
            source = findEntity(source, activeEffect.getSourceId(), targetData);

            float damage = getScaledValue(activeEffect.getStackCount());
            //MKWeapons.LOGGER.info("bleed damage {} {} from {}", damage, activeEffect, source);
            LivingEntity target = targetData.getEntity();
            target.attackEntityFrom(MKDamageSource.causeEffectDamage(CoreDamageTypes.BleedDamage, "mkweapons.effect.bleed",
                    source, source, getModifierScale()), damage);

            PacketHandler.sendToTrackingAndSelf(
                    new ParticleEffectSpawnPacket(
                            MKWeaponsParticles.DRIPPING_BLOOD,
                            ParticleEffects.DIRECTED_SPOUT, 8, 1,
                            target.getPosX(), target.getPosY() + target.getHeight() * .75,
                            target.getPosZ(), target.getWidth() / 2.0, 0.5, target.getWidth() / 2.0, 3,
                            target.getUpVector(0)), target);
            return true;
        }
    }
}
