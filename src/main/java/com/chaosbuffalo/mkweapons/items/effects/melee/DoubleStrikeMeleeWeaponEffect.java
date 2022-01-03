package com.chaosbuffalo.mkweapons.items.effects.melee;

import com.chaosbuffalo.mkcore.MKCore;
import com.chaosbuffalo.mkcore.core.CombatExtensionModule;
import com.chaosbuffalo.mkcore.network.PacketHandler;
import com.chaosbuffalo.mkcore.network.ResetAttackSwingPacket;
import com.chaosbuffalo.mkcore.utils.EntityUtils;
import com.chaosbuffalo.mkweapons.MKWeapons;
import com.chaosbuffalo.mkweapons.items.weapon.IMKMeleeWeapon;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class DoubleStrikeMeleeWeaponEffect extends BaseMeleeWeaponEffect {

    private double chance;
    public static final ResourceLocation NAME = new ResourceLocation(MKWeapons.MODID, "weapon_effect.double_strike");

    public DoubleStrikeMeleeWeaponEffect(double chance){
        this();
        this.chance = chance;
    }

    public DoubleStrikeMeleeWeaponEffect(){
        super(NAME, TextFormatting.DARK_AQUA);
    }

    @Override
    public <D> void deserialize(Dynamic<D> dynamic) {
        super.deserialize(dynamic);
        setChance(dynamic.get("chance").asDouble(0.05));
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops) {
        return ops.mergeToMap(super.serialize(ops), ImmutableMap.of(
                ops.createString("chance"), ops.createDouble(getChance()))).result()
                .orElse(ops.createMap(ImmutableMap.of()));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
        super.addInformation(stack, worldIn, tooltip);
        if (Screen.hasShiftDown()){
            tooltip.add(new StringTextComponent(I18n.format("mkweapons.weapon_effect.double_strike.description",
                    chance * 100.0f)));
        }
    }

    @Override
    public void postAttack(IMKMeleeWeapon weapon, ItemStack stack, LivingEntity attacker) {
        if (attacker.getEntityWorld().isRemote()){
            return;
        }
        MKCore.getEntityData(attacker).ifPresent(cap -> {
            double roll = attacker.getRNG().nextDouble();
            if (roll >= (1.0 - chance)){
                CombatExtensionModule combatModule = cap.getCombatExtension();
                double cooldownPeriod = EntityUtils.getCooldownPeriod(attacker);
                combatModule.addEntityTicksSinceLastSwing((int) cooldownPeriod);
                if (attacker instanceof ServerPlayerEntity){
                    PacketHandler.sendMessage(new ResetAttackSwingPacket(combatModule.getEntityTicksSinceLastSwing()),
                            (ServerPlayerEntity) attacker);
                }
            }
        });
    }
}
