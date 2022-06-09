package com.chaosbuffalo.mkweapons;

import com.chaosbuffalo.mkcore.abilities.MKAbility;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.player.PlayerEntity;

public class ClientUtils {

    public static float getClientSkillLevel(Attribute skill) {
        Minecraft mc = Minecraft.getInstance();
        PlayerEntity player = mc.player;
        if (player != null) {
            return MKAbility.getSkillLevel(player, skill);
        }
        return 0.0f;
    }
}
