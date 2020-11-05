package com.chaosbuffalo.mkweapons;

import com.chaosbuffalo.mkcore.ClientEventHandler;
import com.chaosbuffalo.mkcore.client.gui.MKOverlay;
import com.chaosbuffalo.mkcore.client.rendering.MKRenderers;
import com.chaosbuffalo.mkweapons.client.particle.BloodDripParticle;
import com.chaosbuffalo.mkweapons.init.MKWeaponsParticles;
import com.chaosbuffalo.mkweapons.items.weapon.types.MeleeWeaponTypes;
import net.minecraft.client.particle.ParticleManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MKWeapons.MODID)
public class MKWeapons
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "mkweapons";

    public MKWeapons() {
        MeleeWeaponTypes.registerWeaponTypes();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        event.getMinecraftSupplier().get().particles.registerFactory(MKWeaponsParticles.DRIPPING_BLOOD,
                BloodDripParticle.BloodDripFactory::new);
    }
}
