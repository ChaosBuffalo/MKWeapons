package com.chaosbuffalo.mkweapons.network;

import com.chaosbuffalo.mkweapons.MKWeapons;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
    private static SimpleChannel networkChannel;
    private static final String VERSION = "1.0";

    public static void setupHandler() {
        networkChannel = NetworkRegistry.newSimpleChannel(new ResourceLocation(MKWeapons.MODID, "packet_handler"),
                () -> VERSION,
                s -> s.equals(VERSION),
                s -> s.equals(VERSION));
        registerMessages();
    }

    public static void registerMessages() {
        int id = 1;
        networkChannel.registerMessage(id++, SyncWeaponTypesPacket.class, SyncWeaponTypesPacket::toBytes,
                SyncWeaponTypesPacket::new, SyncWeaponTypesPacket::handle);
    }

    public static SimpleChannel getNetworkChannel() {
        return networkChannel;
    }

    public static <T> void sendMessageToServer(T msg) {
        networkChannel.sendToServer(msg);
    }

    public static <T> void sendMessage(T msg, ServerPlayerEntity target) {
        PacketDistributor.PLAYER.with(() -> target)
                .send(getNetworkChannel().toVanillaPacket(msg, NetworkDirection.PLAY_TO_CLIENT));
    }

    public static <T> void sendToTracking(T msg, Entity entity) {
        PacketDistributor.TRACKING_ENTITY.with(() -> entity)
                .send(getNetworkChannel().toVanillaPacket(msg, NetworkDirection.PLAY_TO_CLIENT));
    }

    public static <T> void sendToTrackingAndSelf(T msg, ServerPlayerEntity player) {
        PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player)
                .send(getNetworkChannel().toVanillaPacket(msg, NetworkDirection.PLAY_TO_CLIENT));
    }

    public static <T> void sendToTrackingMaybeSelf(T msg, Entity entity) {
        if (entity.world.isRemote)
            return;

        if (entity instanceof ServerPlayerEntity) {
            sendToTrackingAndSelf(msg, (ServerPlayerEntity) entity);
        } else {
            sendToTracking(msg, entity);
        }
    }

    public static <T> void sendToAll(T msg) {
        PacketDistributor.ALL.noArg().send(getNetworkChannel().toVanillaPacket(msg, NetworkDirection.PLAY_TO_CLIENT));

    }
}
