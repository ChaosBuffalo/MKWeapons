package com.chaosbuffalo.mkweapons;

import com.chaosbuffalo.mkcore.ClientEventHandler;
import com.chaosbuffalo.mkcore.CoreCapabilities;
import com.chaosbuffalo.mkcore.client.gui.MKOverlay;
import com.chaosbuffalo.mkcore.client.rendering.MKRenderers;
import com.chaosbuffalo.mkcore.command.MKCommand;
import com.chaosbuffalo.mkcore.core.persona.PersonaManager;
import com.chaosbuffalo.mkcore.mku.PersonaTest;
import com.chaosbuffalo.mkcore.network.PacketHandler;
import com.chaosbuffalo.mkweapons.capabilities.IArrowData;
import com.chaosbuffalo.mkweapons.capabilities.WeaponsCapabilities;
import com.chaosbuffalo.mkweapons.client.particle.BloodDripParticle;
import com.chaosbuffalo.mkweapons.event.MKWeaponsEventHandler;
import com.chaosbuffalo.mkweapons.event.WeaponsCapabilityHandler;
import com.chaosbuffalo.mkweapons.init.MKWeaponsParticles;
import com.chaosbuffalo.mkweapons.items.weapon.types.MeleeWeaponTypes;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MKWeapons.MODID)
public class MKWeapons
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "mkweapons";

    public MKWeapons() {
        MeleeWeaponTypes.registerWeaponTypes();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        WeaponsCapabilities.registerCapabilities();
        MKWeaponsEventHandler.registerCombatTriggers();
    }

    public static LazyOptional<IArrowData> getArrowCapability(AbstractArrowEntity entity){
        return entity.getCapability(WeaponsCapabilities.ARROW_DATA_CAPABILITY);
    }
}
