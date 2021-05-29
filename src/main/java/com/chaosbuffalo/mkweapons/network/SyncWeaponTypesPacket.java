package com.chaosbuffalo.mkweapons.network;

import com.chaosbuffalo.mkcore.MKCore;
import com.chaosbuffalo.mkweapons.items.weapon.types.IMeleeWeaponType;
import com.chaosbuffalo.mkweapons.items.weapon.types.MeleeWeaponTypes;
import com.chaosbuffalo.mkweapons.items.weapon.types.WeaponTypeManager;
import com.mojang.serialization.Dynamic;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SyncWeaponTypesPacket {
    private final Map<ResourceLocation, CompoundNBT> data;


    public SyncWeaponTypesPacket(Collection<IMeleeWeaponType> meleeTypes) {

        data = new HashMap<>();
        for (IMeleeWeaponType meleeType : meleeTypes) {
            INBT dyn = meleeType.serialize(NBTDynamicOps.INSTANCE);
            if (dyn instanceof CompoundNBT) {
                data.put(meleeType.getName(), (CompoundNBT) dyn);
            } else {
                throw new RuntimeException(String.format("Melee Weapon Type %s did not serialize to a CompoundNBT!",
                        meleeType.getName()));
            }
        }
    }

    public void toBytes(PacketBuffer buffer) {
        buffer.writeInt(data.size());
        for (Map.Entry<ResourceLocation, CompoundNBT> meleeData : data.entrySet()) {
            buffer.writeResourceLocation(meleeData.getKey());
            buffer.writeCompoundTag(meleeData.getValue());
        }
    }

    public SyncWeaponTypesPacket(PacketBuffer buffer) {
        int count = buffer.readInt();
        data = new HashMap<>();
        for (int i = 0; i < count; i++) {
            ResourceLocation typeName = buffer.readResourceLocation();
            CompoundNBT typeData = buffer.readCompoundTag();
            data.put(typeName, typeData);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        MKCore.LOGGER.debug("Handling player abilities update packet");
        ctx.enqueueWork(() -> {
            if (Minecraft.getInstance().player != null){
                WeaponTypeManager.handleMKWeaponReloadForPlayerPre(Minecraft.getInstance().player);
            }
            for (Map.Entry<ResourceLocation, CompoundNBT> meleeWeaponPair : data.entrySet()) {
                IMeleeWeaponType weaponType = MeleeWeaponTypes.getWeaponType(meleeWeaponPair.getKey());
                if (weaponType != null) {
                    MKCore.LOGGER.debug("Updating melee weapon type with server data: {}", meleeWeaponPair.getKey());
                    weaponType.deserialize(new Dynamic<>(NBTDynamicOps.INSTANCE, meleeWeaponPair.getValue()));
                } else {
                    MKCore.LOGGER.warn("Skipping melee weapon type update for {}", meleeWeaponPair.getKey());
                }
            }
            WeaponTypeManager.refreshAllWeapons();
            if (Minecraft.getInstance().player != null){
                WeaponTypeManager.handleMKWeaponReloadForPlayerPost(Minecraft.getInstance().player);
            }
        });
        ctx.setPacketHandled(true);
    }
}
