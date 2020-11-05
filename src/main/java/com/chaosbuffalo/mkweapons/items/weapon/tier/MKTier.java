package com.chaosbuffalo.mkweapons.items.weapon.tier;

import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;

public class MKTier implements IItemTier{
    IItemTier itemTier;

    public MKTier(IItemTier tier){
        itemTier = tier;
    }

    @Override
    public int getMaxUses() {
        return itemTier.getMaxUses();
    }

    @Override
    public float getEfficiency() {
        return itemTier.getEfficiency();
    }

    @Override
    public float getAttackDamage() {
        return itemTier.getAttackDamage();
    }

    @Override
    public int getHarvestLevel() {
        return itemTier.getHarvestLevel();
    }

    @Override
    public int getEnchantability() {
        return itemTier.getEnchantability();
    }

    @Override
    public Ingredient getRepairMaterial() {
        return itemTier.getRepairMaterial();
    }
}
