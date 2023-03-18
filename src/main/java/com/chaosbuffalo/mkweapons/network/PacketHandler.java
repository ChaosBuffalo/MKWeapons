package com.chaosbuffalo.mkweapons.network;

import com.chaosbuffalo.mkweapons.MKWeapons;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
    private static SimpleChannel networkChannel;
    private static final String VERSION = "1.0";
    private static int nextPacketId = 1;

    public static void setupHandler() {
        networkChannel = NetworkRegistry.newSimpleChannel(new ResourceLocation(MKWeapons.MODID, "packet_handler"),
                () -> VERSION,
                VERSION::equals,
                VERSION::equals);
        registerMessages();
    }

    private static int nextId() {
        return nextPacketId++;
    }

    public static void registerMessages() {
        networkChannel.messageBuilder(SyncWeaponTypesPacket.class, nextId(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncWeaponTypesPacket::toBytes)
                .decoder(SyncWeaponTypesPacket::new)
                .consumer(SyncWeaponTypesPacket::handle)
                .add();
    }

    public static SimpleChannel getNetworkChannel() {
        return networkChannel;
    }

}
