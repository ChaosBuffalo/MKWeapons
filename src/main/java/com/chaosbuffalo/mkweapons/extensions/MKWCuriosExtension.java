package com.chaosbuffalo.mkweapons.extensions;

import net.minecraftforge.fml.InterModComms;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

public class MKWCuriosExtension {

    public static void sendExtension() {
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE,
                () -> {
                    return SlotTypePreset.RING.getMessageBuilder().size(2).build();
                });
    }
}
