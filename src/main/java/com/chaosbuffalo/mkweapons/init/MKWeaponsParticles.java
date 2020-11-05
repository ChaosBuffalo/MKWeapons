package com.chaosbuffalo.mkweapons.init;

import com.chaosbuffalo.mkweapons.MKWeapons;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(MKWeapons.MODID)
@Mod.EventBusSubscriber(modid = MKWeapons.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MKWeaponsParticles {

    @ObjectHolder("dripping_blood")
    public static BasicParticleType DRIPPING_BLOOD;

    @SubscribeEvent
    public static void registerParticles(RegistryEvent.Register<ParticleType<?>> evt){
        BasicParticleType drippingBlood = new BasicParticleType(false);
        drippingBlood.setRegistryName(MKWeapons.MODID, "dripping_blood");
        evt.getRegistry().register(drippingBlood);
    }
}
