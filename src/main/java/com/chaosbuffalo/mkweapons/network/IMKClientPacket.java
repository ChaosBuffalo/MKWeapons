package com.chaosbuffalo.mkweapons.network;

import net.minecraftforge.fml.network.simple.SimpleChannel;

public interface IMKClientPacket {
    SimpleChannel getNetworkChannel();

    default <T> void sendMessageToServer() {
        getNetworkChannel().sendToServer(this);
    }
}
