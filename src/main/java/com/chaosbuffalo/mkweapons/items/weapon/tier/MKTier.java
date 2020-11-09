package com.chaosbuffalo.mkweapons.items.weapon.tier;

import com.chaosbuffalo.mkweapons.items.weapon.effects.melee.IMeleeWeaponEffect;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;

import java.util.Arrays;
import java.util.List;

public class MKTier implements IItemTier{
    private final IItemTier itemTier;
    private final List<IMeleeWeaponEffect> weaponEffects;
    private final String name;
    private final Tag<Item> majorIngredient;

    public MKTier(IItemTier tier, String name, Tag<Item> tag, IMeleeWeaponEffect... effects){
        itemTier = tier;
        this.name = name;
        weaponEffects = Arrays.asList(effects);
        majorIngredient = tag;
    }

    public Tag<Item> getMajorIngredient() {
        return majorIngredient;
    }

    public String getName() {
        return name;
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

    public List<IMeleeWeaponEffect> getMeleeWeaponEffects() {
        return weaponEffects;
    }
}
