package com.chaosbuffalo.mkweapons.items.effects.melee;


import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public abstract class SwingMeleeWeaponEffect extends BaseMeleeWeaponEffect {
    private int numberOfHits;
    private double perHit;

    public SwingMeleeWeaponEffect(ResourceLocation name, TextFormatting color, int numberOfHits, double perHit){
        this(name, color);
        this.numberOfHits = numberOfHits;
        this.perHit = perHit;
    }

    public SwingMeleeWeaponEffect(ResourceLocation name, TextFormatting color){
        super(name, color);
    }

    public void setNumberOfHits(int numberOfHits) {
        this.numberOfHits = numberOfHits;
    }

    public void setPerHit(double perHit) {
        this.perHit = perHit;
    }

    @Override
    public <D> void deserialize(Dynamic<D> dynamic) {

        super.deserialize(dynamic);
        setNumberOfHits(dynamic.get("numberOfHits").asInt(5));
        setPerHit(dynamic.get("perHit").asDouble(.1));
    }

    @Override
    public <D> D serialize(DynamicOps<D> ops) {
        return ops.mergeToMap(super.serialize(ops), ImmutableMap.of(
           ops.createString("numberOfHits"), ops.createInt(getNumberOfHits()),
           ops.createString("perHit"), ops.createDouble(getPerHit())
        )).result().orElse(ops.createMap(ImmutableMap.of()));
    }

    public double getPerHit() {
        return perHit;
    }

    public int getNumberOfHits() {
        return numberOfHits;
    }
}
