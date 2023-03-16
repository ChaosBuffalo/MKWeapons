package com.chaosbuffalo.mkweapons.init;

import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.client.particle.BloodDripParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
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
    public static void registerParticles(RegistryEvent.Register<ParticleType<?>> evt) {
        BasicParticleType drippingBlood = new BasicParticleType(false);
        drippingBlood.setRegistryName(MKWeapons.MODID, "dripping_blood");
        evt.getRegistry().register(drippingBlood);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerParticleFactory(ParticleFactoryRegisterEvent evt) {
        Minecraft.getInstance().particles.registerFactory(MKWeaponsParticles.DRIPPING_BLOOD,
                BloodDripParticle.BloodDripFactory::new);
    }
}
