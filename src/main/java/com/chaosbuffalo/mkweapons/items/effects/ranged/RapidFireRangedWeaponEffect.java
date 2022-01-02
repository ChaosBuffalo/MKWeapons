package com.chaosbuffalo.mkweapons.items.effects.ranged;

import com.chaosbuffalo.mkcore.MKCore;
import com.chaosbuffalo.mkweapons.MKWeapons;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class RapidFireRangedWeaponEffect extends BaseRangedWeaponEffect{
    private int maxHits;
    private float perHitReduction;
    public static final ResourceLocation NAME = new ResourceLocation(MKWeapons.MODID, "weapon_effect.rapid_fire");

    public RapidFireRangedWeaponEffect(int maxHits, float perHitReduction){
        this();
        this.maxHits = maxHits;
        this.perHitReduction = perHitReduction;
    }

    public RapidFireRangedWeaponEffect(){
        super(NAME, TextFormatting.DARK_RED);
    }

    @Override
    public <D> void deserialize(Dynamic<D> dynamic) {
        super.deserialize(dynamic);
        maxHits = dynamic.get("maxHits").asInt(5);
        perHitReduction = dynamic.get("perHit").asFloat(.10f);
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops) {
        return ops.mergeToMap(super.serialize(ops), ImmutableMap.of(
                ops.createString("maxHits"), ops.createInt(maxHits),
                ops.createString("perHit"), ops.createFloat(perHitReduction)
        )).result().orElse(ops.createMap(ImmutableMap.of()));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn,
                               List<ITextComponent> tooltip) {
        super.addInformation(stack, worldIn, tooltip);
        if (Screen.hasShiftDown()){
            tooltip.add(new StringTextComponent(I18n.format("mkweapons.weapon_effect.rapid_fire.description",
                    perHitReduction * 100.0f, maxHits * perHitReduction * 100.0f)));
        }
    }

    @Override
    public float modifyDrawTime(float inTime, ItemStack item, LivingEntity entity) {
        return MKCore.getEntityData(entity).map(cap -> {
            int totalToReduce = Math.min(cap.getCombatExtension().getCurrentProjectileHitCount(), maxHits);
            float timeReduction = totalToReduce * perHitReduction;
            return (1.0f - timeReduction) * inTime;
        }).orElse(inTime);

    }
}
