package com.chaosbuffalo.mkweapons.items;

import com.chaosbuffalo.mkcore.test.abilities.EmberAbility;
import com.chaosbuffalo.mkweapons.capabilities.WeaponsCapabilities;
import com.chaosbuffalo.mkweapons.items.effects.melee.StunMeleeWeaponEffect;
import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SEntityEquipmentPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class TestNBTWeaponEffectItem extends Item {

    public TestNBTWeaponEffectItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (!worldIn.isRemote() && handIn.equals(Hand.MAIN_HAND)) {
            if (playerIn.getHeldItemOffhand().getItem() instanceof IMKMeleeWeapon) {
                playerIn.getHeldItemOffhand().getCapability(WeaponsCapabilities.WEAPON_DATA_CAPABILITY).ifPresent(cap -> {
                    cap.addMeleeWeaponEffect(new StunMeleeWeaponEffect(0.5, 2));
                    cap.setAbilityId(EmberAbility.INSTANCE.getAbilityId());
                });
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) playerIn;
                serverPlayer.connection.sendPacket(new SEntityEquipmentPacket(playerIn.getEntityId(),
                        Lists.newArrayList(Pair.of(EquipmentSlotType.OFFHAND, playerIn.getHeldItemOffhand()))));
                return ActionResult.resultSuccess(playerIn.getHeldItemMainhand());
            }
        }
        return ActionResult.resultSuccess(playerIn.getHeldItemMainhand());
    }
}
