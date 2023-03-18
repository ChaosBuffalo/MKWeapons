package com.chaosbuffalo.mkweapons.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public interface IMKServerPacket {

    SimpleChannel getNetworkChannel();

    default void sendTo(ServerPlayerEntity target) {
        PacketDistributor.PLAYER.with(() -> target)
                .send(getNetworkChannel().toVanillaPacket(this, NetworkDirection.PLAY_TO_CLIENT));
    }

    default void sendToAll() {
        PacketDistributor.ALL.noArg().send(getNetworkChannel().toVanillaPacket(this, NetworkDirection.PLAY_TO_CLIENT));
    }

    default <T> void sendToTracking(Entity entity) {
        PacketDistributor.TRACKING_ENTITY.with(() -> entity)
                .send(getNetworkChannel().toVanillaPacket(this, NetworkDirection.PLAY_TO_CLIENT));
    }

    default <T> void sendToTrackingAndSelf(ServerPlayerEntity player) {
        PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player)
                .send(getNetworkChannel().toVanillaPacket(this, NetworkDirection.PLAY_TO_CLIENT));
    }

    default <T> void sendToTrackingMaybeSelf(Entity entity) {
        if (entity.world.isRemote)
            return;

        if (entity instanceof ServerPlayerEntity) {
            sendToTrackingAndSelf((ServerPlayerEntity) entity);
        } else {
            sendToTracking(entity);
        }
    }
}
