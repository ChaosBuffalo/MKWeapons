package com.chaosbuffalo.mkweapons.items.weapon.types;

import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.capabilities.IWeaponData;
import com.chaosbuffalo.mkweapons.capabilities.WeaponsCapabilities;
import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import com.chaosbuffalo.mkweapons.network.PacketHandler;
import com.chaosbuffalo.mkweapons.network.SyncWeaponTypesPacket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WeaponTypeManager extends JsonReloadListener {
    private MinecraftServer server;
    public static final List<IMKMeleeWeapon> MELEE_WEAPONS = new ArrayList<>();
    private boolean serverStarted = false;

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();


    public WeaponTypeManager() {
        super(GSON, "melee_weapon_types");
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void subscribeEvent(AddReloadListenerEvent event){
        event.addListener(this);
    }


    public static void addMeleeWeapon(IMKMeleeWeapon weapon){
        MELEE_WEAPONS.add(weapon);
    }


    public void syncToPlayers() {
        SyncWeaponTypesPacket updatePacket = new SyncWeaponTypesPacket(MeleeWeaponTypes.WEAPON_TYPES.values());
        PacketHandler.sendToAll(updatePacket);
    }

    @SubscribeEvent
    public void serverStart(FMLServerAboutToStartEvent event) {
        server = event.getServer();
        serverStarted = true;
    }


    @SuppressWarnings("unused")
    @SubscribeEvent
    public void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        MKWeapons.LOGGER.debug("Player logged in weapon type manager");
        if (event.getPlayer() instanceof ServerPlayerEntity) {
            SyncWeaponTypesPacket updatePacket = new SyncWeaponTypesPacket(MeleeWeaponTypes.WEAPON_TYPES.values());
            MKWeapons.LOGGER.debug("Sending {} update packet", event.getPlayer());
            PacketHandler.sendMessage(updatePacket, (ServerPlayerEntity) event.getPlayer());
        }
    }

    public static void handleMKWeaponReloadForPlayerPre(PlayerEntity player){
        ItemStack mainHand = player.getHeldItemMainhand();
        if (mainHand.getItem() instanceof IMKMeleeWeapon){
            player.getAttributeManager().removeModifiers(mainHand.getAttributeModifiers(EquipmentSlotType.MAINHAND));
        }
    }

    public static void refreshAllWeapons(){
        for (IMKMeleeWeapon weapon : MELEE_WEAPONS){
            weapon.reload();
        }
    }

    public static void handleMKWeaponReloadForPlayerPost(PlayerEntity player){
        ItemStack mainHand = player.getHeldItemMainhand();
        if (mainHand.getItem() instanceof IMKMeleeWeapon){
            player.getAttributeManager().reapplyModifiers(mainHand.getAttributeModifiers(EquipmentSlotType.MAINHAND));
        }
        for (ItemStack item : player.inventory.mainInventory){
            if (item != ItemStack.EMPTY){
                item.getCapability(WeaponsCapabilities.WEAPON_DATA_CAPABILITY)
                        .ifPresent(IWeaponData::markCacheDirty);
            }
        }
        for (ItemStack item : player.inventory.offHandInventory){
            if (item != ItemStack.EMPTY){
                item.getCapability(WeaponsCapabilities.WEAPON_DATA_CAPABILITY)
                        .ifPresent(IWeaponData::markCacheDirty);
            }
        }
    }

    private boolean parse(ResourceLocation loc, JsonObject json) {
        MKWeapons.LOGGER.debug("Parsing Weapon Type Json for {}", loc);
        IMeleeWeaponType weaponType = MeleeWeaponTypes.getWeaponType(loc);
        if (weaponType == null) {
            MKWeapons.LOGGER.warn("Failed to parse weapon type data for : {}", loc);
            return false;
        }
        weaponType.deserialize(new Dynamic<>(JsonOps.INSTANCE, json));
        return true;
    }

    @SubscribeEvent
    public void serverStop(FMLServerStoppingEvent event) {
        serverStarted = false;
        server = null;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
        MKWeapons.LOGGER.debug("Loading melee weapon type definitions from Json");
        boolean wasChanged = false;
        if (serverStarted){
            List<ServerPlayerEntity> players = server.getPlayerList().getPlayers();
            for (ServerPlayerEntity entity : players){
                handleMKWeaponReloadForPlayerPre(entity);
            }
        }
        for (Map.Entry<ResourceLocation, JsonElement> entry : objectIn.entrySet()) {
            ResourceLocation resourcelocation = entry.getKey();
            MKWeapons.LOGGER.debug("Found file: {}", resourcelocation);
            if (resourcelocation.getPath().startsWith("_"))
                continue; //Forge: filter anything beginning with "_" as it's used for metadata.
            if (parse(entry.getKey(), entry.getValue().getAsJsonObject())) {
                wasChanged = true;
            }
        }
        refreshAllWeapons();
        if (serverStarted){
            List<ServerPlayerEntity> players = server.getPlayerList().getPlayers();
            for (ServerPlayerEntity entity : players){
                handleMKWeaponReloadForPlayerPost(entity);
            }
        }

        if (serverStarted && wasChanged) {
            syncToPlayers();
        }
    }
}
