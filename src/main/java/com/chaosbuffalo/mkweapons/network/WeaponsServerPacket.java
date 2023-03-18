package com.chaosbuffalo.mkweapons.network;

import net.minecraftforge.fml.network.simple.SimpleChannel;

public class WeaponsServerPacket implements IMKServerPacket {
    @Override
    public SimpleChannel getNetworkChannel() {
        return PacketHandler.getNetworkChannel();
    }
}
